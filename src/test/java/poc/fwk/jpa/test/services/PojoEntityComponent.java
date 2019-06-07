package poc.fwk.jpa.test.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import poc.fwk.jpa.test.entities.PojoEntity;
import poc.fwk.jpa.test.repositories.PojoEntityRepository;

@Component
@RequiredArgsConstructor
public class PojoEntityComponent {

	@Autowired
	private PojoEntityRepository pojoEntityRepository;

	public PojoEntity getEntity(Integer id) {
		return pojoEntityRepository.findOne(id);
	}
}
