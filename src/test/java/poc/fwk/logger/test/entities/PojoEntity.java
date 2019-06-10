package poc.fwk.logger.test.entities;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import lombok.Data;

@Data
@Entity
public class PojoEntity {

	@Id
	@GeneratedValue
	private Integer id;

	@ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	private PojoEntityElement element;

	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	private List<PojoEntityEntry> entries;

}
