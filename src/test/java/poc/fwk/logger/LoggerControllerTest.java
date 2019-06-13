package poc.fwk.logger;

import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import poc.fwk.logger.test.LoggerTestBase;
import poc.fwk.test.SpringTestContext;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = SpringTestContext.class,
		properties = "poc.fwk.logger.controller.level=debug")
@AutoConfigureMockMvc
public class LoggerControllerTest extends LoggerTestBase {

	@Autowired
	private MockMvc mockMvc;

	@Test
	public void testLogController() throws Exception {
		mockMvc.perform(get("/logValue?languaje=es")).andExpect(status().isOk());
		assertLog("/logs/testLogValueResponse");
	}

	@Test
	public void testLogControllerNoLogClass() throws Exception {
		mockMvc.perform(get("/noLogClass")).andExpect(status().isOk());
		assertTrue(getLog().isEmpty());
	}

	@Test
	public void testLogControllerNoLogMethod() throws Exception {
		mockMvc.perform(get("/noLogMethod")).andExpect(status().isOk());
		assertTrue(getLog().isEmpty());
	}

	private void assertLog(String logResult) throws IOException {
		String log = getLog();
		IOUtils.readLines(new FileInputStream(IOUtils.resourceToURL(logResult).getFile()),
				StandardCharsets.UTF_8)
				.forEach(line -> assertTrue("NOT PRESENT: " + line, log.contains(line)));
	}
}
