package poc.fwk.logger;

import java.io.Closeable;
import java.lang.reflect.Method;
import java.time.Duration;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.event.Level;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.hibernate5.Hibernate5Module;
import com.fasterxml.jackson.datatype.hibernate5.Hibernate5Module.Feature;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class LoggerAdvice implements ApplicationContextAware {

	private static final String MESSAGE_INFO = "%sCALL: %s TIME: %s";

	private static final String MESSAGE_DEBUG = "\n%sCALL: %s\nINPUT: %s\nOUTPUT: %s\nTIME: %s";

	private static final String SESSION_ID_MESSAGE = "SESSION_ID: %s ";

	private static final String REQUEST_MESSAGE = "REQUEST: [%s] %s ";

	private static final String LIST_OPEN = "[";

	private static final String LIST_CLOSE = "]";

	private static final String LIST_SPLIT = StringUtils.LF;

	private static final String VALUE_JOIN = "=";

	private static final String VALUE_SPLIT = ",\n";

	private final ObjectMapper objectMapper;

	private boolean isWebEnvironment = false;

	public LoggerAdvice() {
		objectMapper = new ObjectMapper();
		objectMapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
		objectMapper.disable(SerializationFeature.FAIL_ON_SELF_REFERENCES);
		objectMapper.disable(SerializationFeature.FAIL_ON_UNWRAPPED_TYPE_IDENTIFIERS);

		Hibernate5Module hibernate5Module = new Hibernate5Module();
		hibernate5Module.enable(Feature.SERIALIZE_IDENTIFIER_FOR_LAZY_NOT_LOADED_OBJECTS);
		hibernate5Module.disable(Feature.USE_TRANSIENT_ANNOTATION);
		objectMapper.registerModule(hibernate5Module);
	}

	public Object log(ProceedingJoinPoint joinPoint, Level level) throws Throwable {
		long time = System.currentTimeMillis();
		Object response;
		try {
			response = joinPoint.proceed();
			log(joinPoint, response, System.currentTimeMillis() - time, level);
		} catch (Throwable t) {
			log(joinPoint, t.getClass().getName(), time, level);
			throw t;
		}
		return response;
	}

	private void log(ProceedingJoinPoint joinPoint, Object response, long time, Level level) {
		try {
			Class<?> targetClass = getTargetClass(joinPoint);
			Logger logger = LoggerFactory.getLogger(targetClass);
			switch (level) {
				case INFO:
					if (logger.isInfoEnabled()) {
						logger.info(getMessage(targetClass, joinPoint, response, time, level));
					}
					break;
				case DEBUG:
					if (logger.isDebugEnabled()) {
						logger.debug(getMessage(targetClass, joinPoint, response, time, level));
					}
					break;
				default:
					if (logger.isTraceEnabled()) {
						logger.trace(getMessage(targetClass, joinPoint, response, time, level));
					}
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}

	private Class<?> getTargetClass(ProceedingJoinPoint joinPoint) {
		Class<?> targetClass = joinPoint.getTarget().getClass();
		for (Class<?> targetInteface : joinPoint.getTarget().getClass().getInterfaces()) {
			MethodSignature signature = MethodSignature.class.cast(joinPoint.getSignature());
			Method targetMethod = ReflectionUtils.findMethod(targetInteface,
					signature.getMethod().getName(), signature.getParameterTypes());
			if (targetMethod != null) {
				targetClass = targetInteface;
				break;
			}
		}
		return targetClass;
	}

	private String getMessage(Class<?> targetClass, ProceedingJoinPoint joinPoint, Object response, long time, Level level) {
		MethodSignature signature = MethodSignature.class.cast(joinPoint.getSignature());
		if (Level.INFO.equals(level)) {
			return String.format(MESSAGE_INFO, getRequestMessage(level),
					getSignature(targetClass, signature),
					Duration.ofMillis(time).toString());
		} else {
			return String.format(MESSAGE_DEBUG, getRequestMessage(level),
					getSignature(targetClass, signature),
					getInput(signature, joinPoint),
					getOutput(signature, response),
					Duration.ofMillis(time).toString());
		}
	}

	private String getSignature(Class<?> targetClass, MethodSignature signature) {
		String signatureString = signature.toString();
		signatureString = StringUtils.replace(signatureString, signature.getDeclaringTypeName(), targetClass.getName());
		return signatureString;
	}

	private Object getInput(MethodSignature signature, ProceedingJoinPoint joinPoint) {
		StringBuilder sb = new StringBuilder();
		Object[] args = joinPoint.getArgs();
		Object[] parameterNames = signature.getParameterNames();
		sb.append(LIST_OPEN);
		if (args.length > 1) {
			sb.append(LIST_SPLIT);
		}
		for (int i = 0; i < args.length; i++) {
			sb.append(parameterNames != null && parameterNames.length > i ? parameterNames[i] : args[i].getClass().getSimpleName());
			sb.append(VALUE_JOIN);
			sb.append(toJsonStringValue(args[i]));
			if (args.length - 1 < i) {
				sb.append(VALUE_SPLIT);
			}
		}
		sb.append(LIST_CLOSE);
		return sb.toString();
	}

	private String getOutput(MethodSignature signature, Object response) {
		String output;
		Method method = signature.getMethod();
		if (void.class.isAssignableFrom(method.getReturnType()) || Void.class.isAssignableFrom(method.getReturnType())) {
			output = method.getReturnType().getSimpleName();
		} else {
			output = toJsonStringValue(response);
		}
		return output;
	}

	private String getRequestMessage(Level level) {
		if (isWebEnvironment && RequestContextHolder.getRequestAttributes() != null) {
			ServletRequestAttributes attributes = ServletRequestAttributes.class.cast(RequestContextHolder.getRequestAttributes());
			HttpServletRequest request = attributes.getRequest();
			return String.format(
					Level.INFO.equals(level) ? SESSION_ID_MESSAGE + REQUEST_MESSAGE : SESSION_ID_MESSAGE + StringUtils.LF + REQUEST_MESSAGE + StringUtils.LF,
					attributes.getSessionId(),
					request.getMethod(),
					StringUtils.isNotEmpty(request.getQueryString()) ? request.getRequestURI() + "?" + request.getQueryString() : request.getRequestURI());
		} else {
			return StringUtils.EMPTY;
		}
	}

	private String toJsonStringValue(Object value) {
		String stringValue = null;
		if (value != null) {
			if (byte[].class.isInstance(value) || Closeable.class.isInstance(value)
					|| isWebEnvironment && (HttpSession.class.isInstance(value)
							|| ServletRequest.class.isInstance(value)
							|| ServletResponse.class.isInstance(value))) {
				stringValue = value.getClass().getSimpleName();
			} else if (isWebEnvironment && ResponseEntity.class.isInstance(value)) {
				ResponseEntity<?> responseEntity = ResponseEntity.class.cast(value);
				stringValue = toJsonStringObjetc(
						new ResponseEntity<>(toJsonStringValue(responseEntity.getBody()),
								responseEntity.getHeaders(),
								responseEntity.getStatusCode()));
			} else {
				stringValue = toJsonStringObjetc(value);
			}
		} else {
			stringValue = String.valueOf(null);
		}
		return stringValue;
	}

	private String toJsonStringObjetc(Object value) {
		String stringValue = null;
		if (value != null) {
			try {
				stringValue = objectMapper.writeValueAsString(value);
			} catch (JsonProcessingException e) {
				stringValue = e.getMessage();
				log.error(e.getMessage());
			}
		} else {
			stringValue = String.valueOf(null);
		}
		return stringValue;
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) {
		isWebEnvironment = StringUtils.containsIgnoreCase(applicationContext.getClass().getName(), "web");
	}

}
