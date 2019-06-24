package poc.fwk.logger.configurers;

import java.lang.reflect.Method;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.event.Level;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.annotation.Order;

import lombok.Setter;
import poc.fwk.logger.LoggerAdvice;

@Order(Integer.MAX_VALUE)
public abstract class LoggerAopBase implements ApplicationContextAware {

	private Boolean loggerCurrentAspectEnabled;

	private String levelCurrentAspect;

	@Autowired
	private LoggerAdvice loggerAdvice;

	@Setter
	private ApplicationContext applicationContext;

	@Value("${poc.fwk.logger.auto.enabled:true}")
	private Boolean autoLoggerEnabled;

	@Value("${poc.fwk.logger.auto.level:info}")
	private String levelValue;

	private Level level;

	public LoggerAopBase() {
		loggerCurrentAspectEnabled = null;
		levelCurrentAspect = null;
	}

	protected LoggerAopBase(Boolean loggerCurrentAspectEnabled, String levelCurrentAspect) {
		this.loggerCurrentAspectEnabled = loggerCurrentAspectEnabled;
		this.levelCurrentAspect = levelCurrentAspect;
	}

	@PostConstruct
	public void initialize() {
		autoLoggerEnabled = ObjectUtils.defaultIfNull(loggerCurrentAspectEnabled, autoLoggerEnabled);
		level = Level.valueOf(ObjectUtils.defaultIfNull(levelCurrentAspect, levelValue).toUpperCase());
	}

	@Pointcut("@within(poc.fwk.logger.annotations.Logger) || @annotation(poc.fwk.logger.annotations.Logger)")
	public void annotatedLogger() {
		// Method is empty as this is just a Pointcut, the implementations are in the advices.
	}

	protected Object log(ProceedingJoinPoint joinPoint) throws Throwable {
		poc.fwk.logger.annotations.Logger logger = AnnotationUtils.findAnnotation(joinPoint.getTarget().getClass(),
				poc.fwk.logger.annotations.Logger.class);
		if (logger == null) {
			MethodSignature signature = MethodSignature.class.cast(joinPoint.getSignature());
			Method method = signature.getMethod();
			logger = AnnotationUtils.findAnnotation(method, poc.fwk.logger.annotations.Logger.class);
		}

		Object result;
		if (logger.enabled()) {
			result = loggerAdvice.log(joinPoint,
					StringUtils.isNotEmpty(logger.level()) ? Level.valueOf(logger.level().toUpperCase()) : level);
		} else {
			result = joinPoint.proceed();
		}
		return result;
	}

	protected Object interceptJoinPoint(ProceedingJoinPoint joinPoint) throws Throwable {
		if (autoLoggerEnabled) { return loggerAdvice.log(joinPoint, level); }
		return joinPoint.proceed();
	}
}
