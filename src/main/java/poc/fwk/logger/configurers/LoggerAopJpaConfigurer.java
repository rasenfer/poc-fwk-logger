package poc.fwk.logger.configurers;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.repository.Repository;

@Configuration
@Aspect
@ConditionalOnClass(Repository.class)
public class LoggerAopJpaConfigurer extends LoggerAopBase {

	@Autowired
	public LoggerAopJpaConfigurer(
			@Value("${poc.fwk.logger.repository.enabled:#{null}}") Boolean loggerRepositoryEnabled,
			@Value("${poc.fwk.logger.repository.level:#{null}}") String loggerRepositoryLevel) {
		super(loggerRepositoryEnabled, loggerRepositoryLevel);
	}

	@Pointcut("within(org.springframework.data.repository.Repository+)")
	public void repository() {
		// Method is empty as this is just a Pointcut, the implementations are in the advices.
	}

	@Around("repository() && !annotatedLogger()")
	public Object interceptRepository(ProceedingJoinPoint joinPoint) throws Throwable {
		return interceptJoinPoint(joinPoint);
	}

}
