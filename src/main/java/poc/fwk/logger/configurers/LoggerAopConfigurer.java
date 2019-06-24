package poc.fwk.logger.configurers;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.context.annotation.Configuration;

@Configuration
@Aspect
public class LoggerAopConfigurer extends LoggerAopBase {

	@Around("annotatedLogger()")
	public Object interceptLogger(ProceedingJoinPoint joinPoint) throws Throwable {
		return log(joinPoint);
	}

}
