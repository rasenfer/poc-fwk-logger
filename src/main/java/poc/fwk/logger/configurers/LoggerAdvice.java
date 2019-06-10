package poc.fwk.logger.configurers;

import java.io.Closeable;
import java.lang.reflect.Method;
import java.time.Duration;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.event.Level;
import org.springframework.aop.aspectj.MethodInvocationProceedingJoinPoint;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class LoggerAdvice implements ApplicationContextAware {

	private static final String MESSAGE = "%s\nINPUT: %s\nOUTPUT: %s\nTIME: %s";

	private static final String LIST_OPEN = "[";

	private static final String LIST_CLOSE = "]";

	private static final String LIST_SPLIT = StringUtils.LF;

	private static final String VALUE_JOIN = "=";

	private static final String VALUE_SPLIT = ",\n";

	private final Level level;

	@Setter
	private ApplicationContext applicationContext;

	public LoggerAdvice(@Value("${poc.fwk.logger.auto.level:info}") String level) {
		this.level = Level.valueOf(level.toUpperCase());
	}

	protected Object log(ProceedingJoinPoint joinPoint) throws Throwable {
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
			Logger logger = LoggerFactory.getLogger(joinPoint.getTarget().getClass());
			logger.error(getMessage(joinPoint, t.getClass().getName(), time), t);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}

	private void log(ProceedingJoinPoint joinPoint, Object response, long time) {
		try {
			Logger logger = LoggerFactory.getLogger(joinPoint.getTarget().getClass());
			switch (level) {
				case WARN:
					if (logger.isWarnEnabled()) {
						logger.warn(getMessage(joinPoint, response, time));
					}
					break;
				case INFO:
					if (logger.isInfoEnabled()) {
						logger.info(getMessage(joinPoint, response, time));
					}
					break;
				case DEBUG:
					if (logger.isDebugEnabled()) {
						logger.debug(getMessage(joinPoint, response, time));
					}
					break;
				default:
					if (logger.isTraceEnabled()) {
						logger.trace(getMessage(joinPoint, response, time));
					}
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}

	private String getMessage(ProceedingJoinPoint joinPoint, Object response, long time) {
		MethodSignature signature = MethodSignature.class.cast(joinPoint.getSignature());
		return String.format(MESSAGE, signature.toString(),
				getInput(signature, MethodInvocationProceedingJoinPoint.class.cast(joinPoint)),
				getOutput(signature, response), Duration.ofMillis(time).toString());
	}

	private Object getInput(MethodSignature signature, MethodInvocationProceedingJoinPoint joinPoint) {
		StringBuilder sb = new StringBuilder();
		Object[] args = joinPoint.getArgs();
		Object[] parameterNames = signature.getParameterNames();
		sb.append(LIST_OPEN);
		if (args.length > 1) {
			sb.append(LIST_SPLIT);
		}
		for (int i = 0; i < args.length; i++) {
			sb.append(parameterNames.length > i ? parameterNames[i] : args[i].getClass().getSimpleName());
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
			if (Iterable.class.isInstance(source) || source.getClass().isArray()) {
				stringValue = toJsonStringStream(Stream.of(source));
			} else if (Closeable.class.isInstance(source)) {
				stringValue = source.getClass().getSimpleName();
			} else {
				stringValue = ReflectionToStringBuilder.toString(source, ToStringStyle.JSON_STYLE);
			}
		} else {
			stringValue = String.valueOf(null);
		}
		return stringValue;
	}

	private String toJsonStringStream(Stream<Object> stream) {
		StringBuilder sb = new StringBuilder(LIST_OPEN);
		stream.forEach(current -> {
			sb.append(toJsonStringObject(current));
			sb.append(LIST_SPLIT);
		});
		sb.append(LIST_CLOSE);
		return StringUtils.removeEnd(sb.toString(), LIST_SPLIT);
	}

}
