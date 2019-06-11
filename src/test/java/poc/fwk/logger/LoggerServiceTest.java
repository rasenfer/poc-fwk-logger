package poc.fwk.logger;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.junit4.SpringRunner;

import poc.fwk.logger.test.LoggerTestBase;
import poc.fwk.logger.test.services.LoggerTestService;
import poc.fwk.logger.test.services.LoggerTestServiceNoLogClass;
import poc.fwk.logger.test.services.LoggerTestServiceNoLogMethods;
import poc.fwk.test.SpringTestContext;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = SpringTestContext.class, webEnvironment = WebEnvironment.NONE)
public class LoggerServiceTest extends LoggerTestBase {

	@Autowired
	private LoggerTestService loggerTestService;

	@Autowired
	private LoggerTestServiceNoLogClass loggerTestServiceNoLogClass;

	@Autowired
	private LoggerTestServiceNoLogMethods loggerTestServiceNoLogMethods;

	@Test
	public void testLogService() throws IOException {
		loggerTestService.returnVoid();
		assertFalse(getLog().isEmpty());
	}

	@Test
	public void testNoLogServiceClass() throws IOException {
		loggerTestServiceNoLogClass.returnVoid();
		assertTrue(getLog().isEmpty());
	}

	@Test
	public void testNoLogServiceMethod() throws IOException {
		loggerTestServiceNoLogMethods.returnVoid();
		assertTrue(getLog().isEmpty());
	}
}
