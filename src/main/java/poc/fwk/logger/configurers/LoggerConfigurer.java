package poc.fwk.logger.configurers;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Aspect
public class LoggerConfigurer extends LoggerBase {

	@Autowired
	public LoggerConfigurer(LoggerAdvice loggerAdvice,
			@Value("${poc.fwk.logger.auto.enabled:true}") boolean autoLoggerEnabled) {
		super(loggerAdvice, autoLoggerEnabled);
	}

	@Override
	@Around("@within(poc.fwk.logger.annotations.Logger) || @annotation(poc.fwk.logger.annotations.Logger)")
	public Object interceptLoggin(ProceedingJoinPoint joinPoint) throws Throwable {
		return super.interceptLoggin(joinPoint);
	}

	@Around("(@within(org.springframework.stereotype.Service)"
			+ " || @within(org.springframework.stereotype.Controller)"
			+ " || @within(org.springframework.web.bind.annotation.RestController))"
			+ " && !@within(poc.fwk.logger.annotations.Logger)"
			+ " && !@annotation(poc.fwk.logger.annotations.Logger)")
	public Object interceptService(ProceedingJoinPoint joinPoint) throws Throwable {
		return interceptJoinPoint(joinPoint);
	}

}
