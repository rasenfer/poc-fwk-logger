package poc.fwk.logger.test;

import java.util.Arrays;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import poc.fwk.logger.annotations.Logger;
import poc.fwk.logger.test.entities.PojoEntity;
import poc.fwk.logger.test.entities.PojoEntityElement;
import poc.fwk.logger.test.entities.PojoEntityEntry;

@Component
@Logger
@RequiredArgsConstructor
public class LoggerTestComponent {

	public PojoEntity returnValue() {
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

		return pojoEntity;
	}

	public void returnVoid(PojoEntity entity) {

	}
}
