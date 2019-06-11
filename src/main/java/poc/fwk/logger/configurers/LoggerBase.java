package poc.fwk.logger.configurers;

import java.lang.reflect.Method;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.annotation.Order;

import lombok.RequiredArgsConstructor;
import lombok.Setter;

@RequiredArgsConstructor
@Order(Integer.MAX_VALUE)
public abstract class LoggerBase implements ApplicationContextAware {

	private final LoggerAdvice loggerAdvice;

	private final boolean autoLoggerEnabled;

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
		if (logger.enabled()) { return loggerAdvice.log(joinPoint); }
		return joinPoint.proceed();
	}

	protected Object interceptJoinPoint(ProceedingJoinPoint joinPoint) throws Throwable {
		if (autoLoggerEnabled) { return loggerAdvice.log(joinPoint); }
		return joinPoint.proceed();
	}
}
