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
import poc.fwk.logger.test.services.LoggerTestServiceAnnotatedClass;
import poc.fwk.logger.test.services.LoggerTestServiceAnnotatedMethod;
import poc.fwk.logger.test.services.LoggerTestServiceNoLogClass;
import poc.fwk.logger.test.services.LoggerTestServiceNoLogMethods;
import poc.fwk.test.SpringTestContext;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = SpringTestContext.class, webEnvironment = WebEnvironment.NONE)
public class LoggerAnnotationTest extends LoggerTestBase {

	@Autowired
	private LoggerTestServiceAnnotatedClass loggerTestServiceAnnotatedClass;

	@Autowired
	private LoggerTestServiceAnnotatedMethod loggerTestServiceAnnotatedMethod;

	@Autowired
	private LoggerTestServiceNoLogClass loggerTestServiceNoLogClass;

	@Autowired
	private LoggerTestServiceNoLogMethods loggerTestServiceNoLogMethods;

	@Test
	public void testLogServiceAnnotatedClass() throws IOException {
		loggerTestServiceAnnotatedClass.returnVoid();
		assertLog();
	}

	@Test
	public void testLogServiceAnnotatedMethod() throws IOException {
		loggerTestServiceAnnotatedMethod.returnVoid();
		assertLog();
	}

	@Test
	public void testNoLogServiceClass() throws IOException {
		loggerTestServiceNoLogClass.returnVoid();
		assertNotLog();
	}

	@Test
	public void testNoLogServiceMethod() throws IOException {
		loggerTestServiceNoLogMethods.returnVoid();
		assertNotLog();
	}

	private void assertLog() throws IOException {
		assertFalse(getLog().isEmpty());
	}

	private void assertNotLog() throws IOException {
		assertTrue(getLog().isEmpty());
	}
}
