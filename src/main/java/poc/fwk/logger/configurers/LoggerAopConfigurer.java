package poc.fwk.logger.configurers;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.boot.autoconfigure.condition.ConditionalOnSingleCandidate;
import org.springframework.context.annotation.Configuration;

@Configuration
@Aspect
@ConditionalOnSingleCandidate(LoggerAopConfigurer.class)
public class LoggerAopConfigurer extends LoggerAopBase {

	@Around("annotatedLogger()")
	public Object interceptLogger(ProceedingJoinPoint joinPoint) throws Throwable {
		return log(joinPoint);
	}

}
