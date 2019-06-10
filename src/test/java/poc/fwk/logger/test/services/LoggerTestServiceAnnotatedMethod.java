package poc.fwk.logger.test.services;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import poc.fwk.logger.annotations.Logger;

@Service
@RequiredArgsConstructor
public class LoggerTestServiceAnnotatedMethod {
	@Logger(enabled = true)
	public void returnVoid() {

	}
}
