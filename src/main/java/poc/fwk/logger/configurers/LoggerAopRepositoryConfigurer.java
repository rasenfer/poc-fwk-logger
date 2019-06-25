package poc.fwk.logger.configurers;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnSingleCandidate;
import org.springframework.context.annotation.Configuration;

@Configuration
@Aspect
@ConditionalOnSingleCandidate(LoggerAopRepositoryConfigurer.class)
public class LoggerAopRepositoryConfigurer extends LoggerAopBase {

	@Autowired
	public LoggerAopRepositoryConfigurer(
			@Value("${poc.fwk.logger.repository.enabled:#{null}}") Boolean loggerRepositoryEnabled,
			@Value("${poc.fwk.logger.repository.level:#{null}}") String loggerRepositoryLevel) {
		super(loggerRepositoryEnabled, loggerRepositoryLevel);
	}

	@Pointcut("@within(org.springframework.stereotype.Repository)")
	public void repository() {
		// Method is empty as this is just a Pointcut, the implementations are in the advices.
	}

	@Around("repository() && !annotatedLogger()")
	public Object interceptService(ProceedingJoinPoint joinPoint) throws Throwable {
		return interceptJoinPoint(joinPoint);
	}

}
