package poc.fwk.logger.test.services;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import poc.fwk.logger.annotations.Logger;

@Service
@RequiredArgsConstructor
@Logger(enabled = true)
public class LoggerTestServiceAnnotatedClass {

	public void returnVoid() {

	}
}
