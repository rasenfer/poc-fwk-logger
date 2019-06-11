package poc.fwk.logger;

import java.io.Closeable;
import java.lang.reflect.Method;
import java.time.Duration;

import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.event.Level;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.hibernate5.Hibernate5Module;
import com.fasterxml.jackson.datatype.hibernate5.Hibernate5Module.Feature;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class LoggerAdvice {

	private final Level level;

	private static final String MESSAGE = "%s\nINPUT: %s\nOUTPUT: %s\nTIME: %s";

	private static final String LIST_OPEN = "[";

	private static final String LIST_CLOSE = "]";

	private static final String LIST_SPLIT = StringUtils.LF;

	private static final String VALUE_JOIN = "=";

	private static final String VALUE_SPLIT = ",\n";

	private final ObjectMapper objectMapper;

	public LoggerAdvice(@Value("${poc.fwk.logger.auto.level:info}") String level) {
		this.level = Level.valueOf(level.toUpperCase());
		objectMapper = new ObjectMapper();
		objectMapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
		objectMapper.disable(SerializationFeature.FAIL_ON_SELF_REFERENCES);
		objectMapper.disable(SerializationFeature.FAIL_ON_UNWRAPPED_TYPE_IDENTIFIERS);

		Hibernate5Module hibernate5Module = new Hibernate5Module();
		hibernate5Module.enable(Feature.SERIALIZE_IDENTIFIER_FOR_LAZY_NOT_LOADED_OBJECTS);
		hibernate5Module.disable(Feature.USE_TRANSIENT_ANNOTATION);
		objectMapper.registerModule(hibernate5Module);
	}

	public Object log(ProceedingJoinPoint joinPoint) throws Throwable {
		long time = System.currentTimeMillis();
		Object response;
		try {
			response = joinPoint.proceed();
			log(joinPoint, response, System.currentTimeMillis() - time);
		} catch (Throwable t) {
			error(joinPoint, t, time);
			throw t;
		}
		return response;
	}

	private void error(ProceedingJoinPoint joinPoint, Throwable t, long time) {
		try {
			Class<?> targetClass = getTargetClass(joinPoint);
			Logger logger = LoggerFactory.getLogger(targetClass);
			logger.error(getMessage(targetClass, joinPoint, t.getClass().getName(), time), t);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}

	private void log(ProceedingJoinPoint joinPoint, Object response, long time) {
		try {
			Class<?> targetClass = getTargetClass(joinPoint);
			Logger logger = LoggerFactory.getLogger(targetClass);
			switch (level) {
				case WARN:
					if (logger.isWarnEnabled()) {
						logger.warn(getMessage(targetClass, joinPoint, response, time));
					}
					break;
				case INFO:
					if (logger.isInfoEnabled()) {
						logger.info(getMessage(targetClass, joinPoint, response, time));
					}
					break;
				case DEBUG:
					if (logger.isDebugEnabled()) {
						logger.debug(getMessage(targetClass, joinPoint, response, time));
					}
					break;
				default:
					if (logger.isTraceEnabled()) {
						logger.trace(getMessage(targetClass, joinPoint, response, time));
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

	private String getMessage(Class<?> targetClass, ProceedingJoinPoint joinPoint, Object response, long time) {
		MethodSignature signature = MethodSignature.class.cast(joinPoint.getSignature());
		return String.format(MESSAGE, getSignature(targetClass, signature),
				getInput(signature, joinPoint),
				getOutput(signature, response), Duration.ofMillis(time).toString());
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
			sb.append(toJsonStringObject(args[i]));
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
			output = toJsonStringObject(response);
		}
		return output;
	}

	private String toJsonStringObject(Object source) {
		String stringValue = null;
		if (source != null) {
			if (Closeable.class.isInstance(source)) {
				stringValue = source.getClass().getSimpleName();
			} else {
				try {
					stringValue = objectMapper.writeValueAsString(source);
				} catch (JsonProcessingException e) {
					stringValue = e.getMessage();
					log.error(e.getMessage());
				}
			}
		} else {
			stringValue = String.valueOf(null);
		}
		return stringValue;
	}

}
