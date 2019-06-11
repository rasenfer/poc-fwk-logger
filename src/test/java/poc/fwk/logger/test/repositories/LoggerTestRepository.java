package poc.fwk.logger.test.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import poc.fwk.logger.test.entities.PojoEntity;

@Repository
public interface LoggerTestRepository extends JpaRepository<PojoEntity, Integer> {

}
