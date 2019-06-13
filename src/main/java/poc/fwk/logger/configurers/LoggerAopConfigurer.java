package poc.fwk.logger.configurers;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.context.annotation.Configuration;

@Configuration
@Aspect
public class LoggerAopConfigurer extends LoggerAopBase {

	@Override
	@Around("@within(poc.fwk.logger.annotations.Logger) || @annotation(poc.fwk.logger.annotations.Logger)")
	public Object interceptLoggin(ProceedingJoinPoint joinPoint) throws Throwable {
		return super.interceptLoggin(joinPoint);
	}

}
