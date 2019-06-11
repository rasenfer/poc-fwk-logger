package poc.fwk.logger.configurers;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.repository.Repository;

import poc.fwk.logger.LoggerAdvice;

@Configuration
@Aspect
@ConditionalOnClass(Repository.class)
public class LoggerAopJpaConfigurer extends LoggerAopBase {

	@Autowired
	public LoggerAopJpaConfigurer(LoggerAdvice loggerAdvice,
			@Value("${poc.fwk.logger.auto.enabled:true}") boolean autoLoggerEnabled) {
		super(loggerAdvice, autoLoggerEnabled);
	}

	@Around("within(org.springframework.data.repository.Repository)"
			+ " && !@within(poc.fwk.logger.annotations.Logger)"
			+ " && !@annotation(poc.fwk.logger.annotations.Logger)")
	public Object interceptRepository(ProceedingJoinPoint joinPoint) throws Throwable {
		return interceptJoinPoint(joinPoint);
	}

}
