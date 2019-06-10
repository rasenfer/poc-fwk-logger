package poc.fwk.logger;

import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.IOException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import poc.fwk.logger.test.LoggerTestBase;
import poc.fwk.logger.test.services.LoggerTestService;
import poc.fwk.test.SpringTestContext;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = SpringTestContext.class, properties = "poc.fwk.logger.auto.enabled=false")
@AutoConfigureMockMvc
public class LoggerAutoDisabledTest extends LoggerTestBase {

	@Autowired
	private LoggerTestService loggerTestService;

	@Autowired
	protected MockMvc mockMvc;

	@Test
	public void testLogServiceAutoDisabled() throws IOException {
		loggerTestService.returnValue();
		assertNotLog();
	}

	@Test
	public void testLogControllerAutoDisabled() throws Exception {
		mockMvc.perform(get("/")).andExpect(status().isOk());
		assertNotLog();
	}

	private void assertNotLog() throws IOException {
		assertTrue(getLog().isEmpty());
	}
}
