package poc.fwk.jpa.test.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import poc.fwk.jpa.test.entities.PojoEntity;
import poc.fwk.jpa.test.repositories.PojoEntityRepository;

@Service
@RequiredArgsConstructor
public class PojoEntityServiceImpl implements PojoEntityService {

	@Autowired
	private PojoEntityRepository pojoEntityRepository;

	@Override
	public PojoEntity getEntity(Integer id) {
		return pojoEntityRepository.findOne(id);
	}

	@Override
	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public PojoEntity testGetEntityNoTransaction(Integer id) {
		return pojoEntityRepository.findOne(id);
	}

	@Override
	public PojoEntity saveEntity(PojoEntity pojoEntity) {
		return pojoEntityRepository.save(pojoEntity);
	}
}
