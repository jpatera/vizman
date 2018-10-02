package eu.japtor.vizman.backend.entity;

import javax.persistence.*;
import java.io.Serializable;

@MappedSuperclass
public class AbstractGenEntity extends AbstractEntity {

	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "id_gen")
	@Override
	public Long getId() {
		return super.getId();
	};
}
