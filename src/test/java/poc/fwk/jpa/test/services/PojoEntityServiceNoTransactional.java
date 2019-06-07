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
@Transactional(propagation = Propagation.NOT_SUPPORTED)
public class PojoEntityServiceNoTransactional {

	@Autowired
	private PojoEntityRepository pojoEntityRepository;

	public PojoEntity getEntity(Integer id) {
		return pojoEntityRepository.findOne(id);
	}
}
