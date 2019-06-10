package poc.fwk.logger.test.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LoggerTestController {

	@GetMapping
	public String getValue() {
		return new String();
	}

}
