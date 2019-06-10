package poc.fwk.logger;

import static org.junit.Assert.assertFalse;
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
import poc.fwk.test.SpringTestContext;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = SpringTestContext.class)
@AutoConfigureMockMvc
public class LoggerControllerTest extends LoggerTestBase {

	@Autowired
	private MockMvc mockMvc;

	@Test
	public void testLogServiceValue() throws Exception {
		mockMvc.perform(get("/")).andExpect(status().isOk());
		assertLog();
	}

	private void assertLog() throws IOException {
		assertFalse(getLog().isEmpty());
	}

}
