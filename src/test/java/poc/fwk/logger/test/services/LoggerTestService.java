package poc.fwk.logger.test.services;

import poc.fwk.logger.test.entities.PojoEntity;

public interface LoggerTestService {

	PojoEntity returnValue();

	void returnVoid(PojoEntity entity);

}
