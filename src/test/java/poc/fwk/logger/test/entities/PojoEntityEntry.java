package poc.fwk.logger.test.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import lombok.Data;

@Data
@Entity
public class PojoEntityEntry {

	@Id
	@GeneratedValue
	private Integer id;

	@Column
	private String value;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	private PojoEntity parent;
}
