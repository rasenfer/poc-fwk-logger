package poc.fwk.logger.test.services;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import poc.fwk.logger.annotations.Logger;

@Service
@RequiredArgsConstructor
public class LoggerTestServiceNoLogMethods {
	@Logger(enabled = false)
	public void returnVoid() {

	}
}
