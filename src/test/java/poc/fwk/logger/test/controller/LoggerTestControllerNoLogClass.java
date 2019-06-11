package poc.fwk.logger.test.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import poc.fwk.logger.annotations.Logger;

@RestController
@Logger(enabled = false)
public class LoggerTestControllerNoLogClass {
	@GetMapping("/noLogClass")
	public String logValue() {
		return new String();
	}
}
