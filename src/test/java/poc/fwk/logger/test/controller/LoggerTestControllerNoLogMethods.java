package poc.fwk.logger.test.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import poc.fwk.logger.annotations.Logger;

@RestController
public class LoggerTestControllerNoLogMethods {
	@GetMapping("/noLogMethod")
	@Logger(enabled = false)
	public String logValue() {
		return new String();
	}
}
