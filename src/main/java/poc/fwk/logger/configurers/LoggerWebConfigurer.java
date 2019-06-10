package poc.fwk.logger.configurers;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@Configuration
@Aspect
@RequiredArgsConstructor
@ConditionalOnClass(RestController.class)
public class LoggerWebConfigurer {

	@Autowired
	private final LoggerAdvice loggerAdvice;

	@Value("${poc.fwk.logger.auto.enabled:true}")
	private boolean autoLoggerEnabled;

	@Around("@within(org.springframework.web.bind.annotation.RestController)"
			+ " && !@within(poc.fwk.logger.annotations.Logger)"
			+ " && !@annotation(poc.fwk.logger.annotations.Logger)")
	Object wrapService(ProceedingJoinPoint joinPoint) throws Throwable {
		if (autoLoggerEnabled) { return loggerAdvice.log(joinPoint); }
		return joinPoint.proceed();
	}

}
