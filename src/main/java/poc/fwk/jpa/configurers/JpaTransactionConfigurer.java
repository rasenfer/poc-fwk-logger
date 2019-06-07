package poc.fwk.jpa.configurers;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import lombok.Setter;

@Configuration
@Aspect
public class JpaTransactionConfigurer implements ApplicationContextAware {

	@Setter
	private ApplicationContext applicationContext;

	@Around("@within(org.springframework.stereotype.Service)"
			+ " && !@within(org.springframework.transaction.annotation.Transactional)"
			+ " && !@annotation(org.springframework.transaction.annotation.Transactional)"
			+ " && !@within(javax.transaction.Transactional)"
			+ " && !@annotation(javax.transaction.Transactional)")
	Object wrapTransaction(ProceedingJoinPoint joinPoint) throws Throwable {
		PlatformTransactionManager txManager = applicationContext.getBean(PlatformTransactionManager.class);
		DefaultTransactionDefinition transactionDefinition = new DefaultTransactionDefinition(TransactionDefinition.PROPAGATION_REQUIRED);
		transactionDefinition.setReadOnly(true);
		txManager.getTransaction(transactionDefinition);
		return joinPoint.proceed();
	}

}
