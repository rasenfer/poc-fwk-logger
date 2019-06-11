package poc.fwk.logger.test;

import org.springframework.stereotype.Component;

import poc.fwk.logger.annotations.Logger;

@Component
public class LoggerTestComponentAnnotatedMethod {
	@Logger(enabled = true)
	public void returnVoid() {

	}
}
