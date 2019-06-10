package poc.fwk.logger.configurers;

import java.lang.reflect.Method;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.AnnotationUtils;

import lombok.RequiredArgsConstructor;
import poc.fwk.logger.annotations.Logger;

@Configuration
@Aspect
@RequiredArgsConstructor
public class LoggerConfigurer {

	@Autowired
	private final LoggerAdvice loggerAdvice;

	@Value("${poc.fwk.logger.auto.enabled:true}")
	private boolean autoLoggerEnabled;

	@Around("@within(poc.fwk.logger.annotations.Logger) || @annotation(poc.fwk.logger.annotations.Logger)")
	Object wrapLoggin(ProceedingJoinPoint joinPoint) throws Throwable {
		Logger logger = AnnotationUtils.findAnnotation(joinPoint.getTarget().getClass(), Logger.class);
		if (logger == null) {
			MethodSignature signature = MethodSignature.class.cast(joinPoint.getSignature());
			Method method = signature.getMethod();
			logger = AnnotationUtils.findAnnotation(method, Logger.class);
		}
		if (logger.enabled()) { return loggerAdvice.log(joinPoint); }
		return joinPoint.proceed();
	}

	@Around("(@within(org.springframework.stereotype.Service)"
			+ " || @within(org.springframework.stereotype.Controller)"
			+ " || @within(org.springframework.web.bind.annotation.RestController))"
			+ " && !@within(poc.fwk.logger.annotations.Logger)"
			+ " && !@annotation(poc.fwk.logger.annotations.Logger)")
	Object wrapService(ProceedingJoinPoint joinPoint) throws Throwable {
		if (autoLoggerEnabled) { return loggerAdvice.log(joinPoint); }
		return joinPoint.proceed();
	}

}
