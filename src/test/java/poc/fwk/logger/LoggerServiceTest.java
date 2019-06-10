package poc.fwk.logger;

import static org.junit.Assert.assertTrue;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.junit4.SpringRunner;

import poc.fwk.logger.test.LoggerTestBase;
import poc.fwk.logger.test.entities.PojoEntity;
import poc.fwk.logger.test.entities.PojoEntityElement;
import poc.fwk.logger.test.entities.PojoEntityEntry;
import poc.fwk.logger.test.services.LoggerTestService;
import poc.fwk.test.SpringTestContext;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = SpringTestContext.class, webEnvironment = WebEnvironment.NONE)
public class LoggerServiceTest extends LoggerTestBase {

	@Autowired
	private LoggerTestService loggerTestService;

	@Test
	public void testLogServiceValue() throws IOException {
		loggerTestService.returnValue();
		assertLog("/logs/testLogServiceValue");
	}

	@Test
	public void testLogServiceVoid() throws IOException {
		PojoEntity pojoEntity = new PojoEntity();
		pojoEntity.setId(1);

		PojoEntityElement element = new PojoEntityElement();
		element.setId(2);
		element.setValue("element");
		pojoEntity.setElement(element);

		PojoEntityEntry entry = new PojoEntityEntry();
		entry.setId(3);
		entry.setValue("entry");
		pojoEntity.setEntries(Arrays.asList(entry));

		loggerTestService.returnVoid(pojoEntity);

		assertLog("/logs/testLogServiceVoid");
	}

	private void assertLog(String logResult) throws IOException {
		String log = getLog();
		IOUtils.readLines(new FileInputStream(IOUtils.resourceToURL(logResult).getFile()),
				StandardCharsets.UTF_8)
				.forEach(line -> assertTrue("NOT PRESENT: " + line, log.contains(line)));
	}
}
