package poc.fwk.logger;

import static org.junit.Assert.assertTrue;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;

import poc.fwk.logger.test.LoggerTestBase;
import poc.fwk.logger.test.entities.PojoEntity;
import poc.fwk.logger.test.entities.PojoEntityElement;
import poc.fwk.logger.test.entities.PojoEntityEntry;
import poc.fwk.logger.test.repositories.LoggerTestRepository;
import poc.fwk.test.SpringTestContext;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = SpringTestContext.class, webEnvironment = WebEnvironment.NONE,
		properties = "poc.fwk.logger.repository.level=debug")
@EnableJpaRepositories(basePackages = "poc.fwk.**.repositories")
@EntityScan(basePackages = "poc.fwk.**.entities")
public class LoggerRepositoryTest extends LoggerTestBase {

	@Autowired
	private LoggerTestRepository loggerTestRepository;

	@Override
	@Before
	public void setUp() throws IOException {

		PojoEntity pojoEntity = new PojoEntity();
		pojoEntity.setId(1);

		PojoEntityElement element = new PojoEntityElement();
		element.setId(2);
		element.setValue("element");
		pojoEntity.setElement(element);

		PojoEntityEntry entry = new PojoEntityEntry();
		entry.setId(3);
		entry.setValue("entry");
		entry.setParent(pojoEntity);
		pojoEntity.setEntries(Arrays.asList(entry));

		loggerTestRepository.save(pojoEntity);

		super.setUp();
	}

	@Test
	@DirtiesContext
	public void testLogGetOne() throws IOException {
		loggerTestRepository.getOne(1);
		assertLog("/logs/testLogGetOne");
	}

	@Test
	@DirtiesContext
	public void testLogFindAll() throws IOException {
		loggerTestRepository.findAll();
		assertLog("/logs/testLogFindAll");
	}

	private void assertLog(String logResult) throws IOException {
		String log = getLog();
		IOUtils.readLines(new FileInputStream(IOUtils.resourceToURL(logResult).getFile()),
				StandardCharsets.UTF_8)
				.forEach(line -> assertTrue("NOT PRESENT: " + line, log.contains(line)));
	}
}
