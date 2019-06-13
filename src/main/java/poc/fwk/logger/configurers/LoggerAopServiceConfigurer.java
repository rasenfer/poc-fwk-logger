package poc.fwk.logger.configurers;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Aspect
public class LoggerAopServiceConfigurer extends LoggerAopBase {

	@Autowired
	public LoggerAopServiceConfigurer(
			@Value("${poc.fwk.logger.service.enabled:#{null}}") Boolean loggerServiceEnabled,
			@Value("${poc.fwk.logger.service.level:#{null}}") String loggerServiceLevel) {
		super(loggerServiceEnabled, loggerServiceLevel);
	}

	@Around("@within(org.springframework.stereotype.Service)"
			+ " && !@within(poc.fwk.logger.annotations.Logger)"
			+ " && !@annotation(poc.fwk.logger.annotations.Logger)")
	public Object interceptService(ProceedingJoinPoint joinPoint) throws Throwable {
		return interceptJoinPoint(joinPoint);
	}

}
