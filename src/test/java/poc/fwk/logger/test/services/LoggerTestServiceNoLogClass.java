package poc.fwk.logger.test.services;

import org.springframework.stereotype.Service;

import poc.fwk.logger.annotations.Logger;

@Service
@Logger(enabled = false)
public class LoggerTestServiceNoLogClass {

	public void returnVoid() {

	}
}
