package poc.fwk.jpa;

import java.util.Arrays;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.hibernate.LazyInitializationException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;

import poc.fwk.jpa.test.entities.PojoEntity;
import poc.fwk.jpa.test.entities.PojoEntityElement;
import poc.fwk.jpa.test.entities.PojoEntityEntry;
import poc.fwk.jpa.test.repositories.PojoEntityRepository;
import poc.fwk.jpa.test.services.PojoEntityComponent;
import poc.fwk.jpa.test.services.PojoEntityService;
import poc.fwk.jpa.test.services.PojoEntityServiceNoTransactional;
import poc.fwk.test.SpringTestContext;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = SpringTestContext.class)
public class ServiceTransactionTest {

	@Autowired
	private PojoEntityRepository pojoEntityRepository;

	@Autowired
	private PojoEntityService pojoEntityService;

	@Autowired
	private PojoEntityServiceNoTransactional pojoEntityServiceNoTransactional;

	@Autowired
	private PojoEntityComponent pojoEntityComponent;

	private Integer id;

	@Before
	public void setUp() {
		id = pojoEntityRepository.saveAndFlush(getPojoEntity()).getId();
	}

	@Test
	@DirtiesContext
	public void testGetEntity() {
		PojoEntity dest = pojoEntityService.getEntity(id);
		ReflectionToStringBuilder.toString(dest);
	}

	@Test
	@DirtiesContext
	public void testSaveEntity() {
		PojoEntity dest = pojoEntityService.saveEntity(getPojoEntity());
		ReflectionToStringBuilder.toString(dest);
	}

	@Test(expected = LazyInitializationException.class)
	@DirtiesContext
	public void testGetEntityNoTransactionMethod() {
		PojoEntity dest = pojoEntityService.testGetEntityNoTransaction(id);
		ReflectionToStringBuilder.toString(dest);
	}

	@Test(expected = LazyInitializationException.class)
	@DirtiesContext
	public void testGetEntityNoTransactionService() {
		PojoEntity dest = pojoEntityServiceNoTransactional.getEntity(id);
		ReflectionToStringBuilder.toString(dest);
	}

	@Test(expected = LazyInitializationException.class)
	@DirtiesContext
	public void testGetEntityNoService() {
		PojoEntity dest = pojoEntityComponent.getEntity(id);
		ReflectionToStringBuilder.toString(dest);
	}

	private PojoEntity getPojoEntity() {
		PojoEntity source = new PojoEntity();

		PojoEntityEntry entry = new PojoEntityEntry();
		entry.setValue("entry");
		source.setEntries(Arrays.asList(entry));

		PojoEntityElement element = new PojoEntityElement();
		element.setValue("element");
		source.setDestEntry(element);

		return source;
	}
}
