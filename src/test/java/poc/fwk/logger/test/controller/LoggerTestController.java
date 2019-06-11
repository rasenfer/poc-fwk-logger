package poc.fwk.logger.test.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LoggerTestController {

	@GetMapping("logValue")
	public String logValue() {
		return new String();
	}

}
