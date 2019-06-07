package poc.fwk.jpa.test.services;

import poc.fwk.jpa.test.entities.PojoEntity;

public interface PojoEntityService {
	PojoEntity getEntity(Integer id);

	PojoEntity saveEntity(PojoEntity pojoEntity);

	PojoEntity testGetEntityNoTransaction(Integer id);
}
