package poc.fwk.logger;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.IOException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import poc.fwk.logger.test.LoggerTestBase;
import poc.fwk.logger.test.repositories.LoggerTestRepository;
import poc.fwk.logger.test.services.LoggerTestService;
import poc.fwk.test.SpringTestContext;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = SpringTestContext.class, properties = "poc.fwk.logger.auto.enabled=false")
@EnableJpaRepositories(basePackages = "poc.fwk.**.repositories")
@EntityScan(basePackages = "poc.fwk.**.entities")
@AutoConfigureMockMvc
public class LoggerAutoDisabledTest extends LoggerTestBase {

	@Autowired
	private LoggerTestService loggerTestService;

	@Autowired
	private LoggerTestRepository loggerTestRepository;

	@Autowired
	protected MockMvc mockMvc;

	@Test
	public void testLogServiceAutoDisabled() throws IOException {
		loggerTestService.returnVoid();
		assertTrue(getLog().isEmpty());
	}

	@Test
	public void testLogControllerAutoDisabled() throws Exception {
		mockMvc.perform(get("/logValue")).andExpect(status().isOk());
		assertTrue(getLog().isEmpty());
	}

	@Test
	public void testLogRepositoryAutoDisabled() throws Exception {
		loggerTestRepository.findAll();
		assertFalse(getLog().contains(LoggerTestRepository.class.getPackage().getName()));
	}
}
