package poc.fwk.logger;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
	public void testLogController() throws Exception {
		mockMvc.perform(get("/logValue")).andExpect(status().isOk());
		assertFalse(getLog().isEmpty());
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

}
