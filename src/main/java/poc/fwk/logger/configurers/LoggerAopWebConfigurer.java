package poc.fwk.logger.configurers;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnSingleCandidate;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RestController;

@Configuration
@Aspect
@ConditionalOnSingleCandidate(LoggerAopWebConfigurer.class)
@ConditionalOnClass(RestController.class)
public class LoggerAopWebConfigurer extends LoggerAopBase {

	@Autowired
	public LoggerAopWebConfigurer(
			@Value("${poc.fwk.logger.controller.enabled:#{null}}") Boolean loggerControllerEnabled,
			@Value("${poc.fwk.logger.controller.level:#{null}}") String loggerControllerLevel) {
		super(loggerControllerEnabled, loggerControllerLevel);
	}

	@Pointcut("@within(org.springframework.web.bind.annotation.RestController)"
			+ "|| @within(org.springframework.stereotype.Controller)")
	public void controller() {
		// Method is empty as this is just a Pointcut, the implementations are in the advices.
	}

	@Around("controller() && !annotatedLogger()")
	public Object interceptController(ProceedingJoinPoint joinPoint) throws Throwable {
		return interceptJoinPoint(joinPoint);
	}

}
