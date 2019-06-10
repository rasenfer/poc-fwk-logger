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
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.annotation.Order;

import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Order(Integer.MAX_VALUE)
public abstract class LoggerBase implements ApplicationContextAware {

	private final boolean autoLoggerEnabled;

	private final Level level;

	@Setter
	private ApplicationContext applicationContext;

	protected Object interceptLoggin(ProceedingJoinPoint joinPoint) throws Throwable {
		poc.fwk.logger.annotations.Logger logger = AnnotationUtils.findAnnotation(joinPoint.getTarget().getClass(),
				poc.fwk.logger.annotations.Logger.class);
		if (logger == null) {
			MethodSignature signature = MethodSignature.class.cast(joinPoint.getSignature());
			Method method = signature.getMethod();
			logger = AnnotationUtils.findAnnotation(method, poc.fwk.logger.annotations.Logger.class);
		}
		if (logger.enabled()) { return log(joinPoint); }
		return joinPoint.proceed();
	}

	protected Object interceptJoinPoint(ProceedingJoinPoint joinPoint) throws Throwable {
		if (autoLoggerEnabled) { return log(joinPoint); }
		return joinPoint.proceed();
	}

	private static final String MESSAGE = "%s\nINPUT: %s\nOUTPUT: %s\nTIME: %s";

	private static final String LIST_OPEN = "[";

	private static final String LIST_CLOSE = "]";

	private static final String LIST_SPLIT = StringUtils.LF;

	private static final String VALUE_JOIN = "=";

	private static final String VALUE_SPLIT = ",\n";

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

	protected void error(ProceedingJoinPoint joinPoint, Throwable t, long time) {
		try {
			Logger logger = LoggerFactory.getLogger(joinPoint.getTarget().getClass());
			logger.error(getMessage(joinPoint, t.getClass().getName(), time), t);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}

	protected void log(ProceedingJoinPoint joinPoint, Object response, long time) {
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

	protected String getMessage(ProceedingJoinPoint joinPoint, Object response, long time) {
		MethodSignature signature = MethodSignature.class.cast(joinPoint.getSignature());
		return String.format(MESSAGE, signature.toString(),
				getInput(signature, MethodInvocationProceedingJoinPoint.class.cast(joinPoint)),
				getOutput(signature, response), Duration.ofMillis(time).toString());
	}

	protected Object getInput(MethodSignature signature, MethodInvocationProceedingJoinPoint joinPoint) {
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

	protected String getOutput(MethodSignature signature, Object response) {
		String output;
		Method method = signature.getMethod();
		if (void.class.isAssignableFrom(method.getReturnType()) || Void.class.isAssignableFrom(method.getReturnType())) {
			output = method.getReturnType().getSimpleName();
		} else {
			output = toJsonStringObject(response);
		}
		return output;
	}

	protected String toJsonStringObject(Object source) {
		String stringValue = null;
		if (source != null) {
			if (Closeable.class.isInstance(source)) {
				stringValue = source.getClass().getSimpleName();
			} else if (Iterable.class.isInstance(source) || source.getClass().isArray()) {
				stringValue = toJsonStringStream(Stream.of(source));
			} else {
				stringValue = ReflectionToStringBuilder.toString(source, ToStringStyle.JSON_STYLE);
			}
		} else {
			stringValue = String.valueOf(null);
		}
		return stringValue;
	}

	protected String toJsonStringStream(Stream<Object> stream) {
		StringBuilder sb = new StringBuilder(LIST_OPEN);
		stream.forEach(current -> {
			sb.append(toJsonStringObject(current));
			sb.append(LIST_SPLIT);
		});
		sb.append(LIST_CLOSE);
		return StringUtils.removeEnd(sb.toString(), LIST_SPLIT);
	}

}
