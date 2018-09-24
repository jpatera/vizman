package eu.japtor.vizman.backend.entity;

import javax.persistence.*;
import java.io.Serializable;

@MappedSuperclass
public class AbstractEntity implements Serializable {

	@Id
	@Column(name = "id", updatable = false, nullable = false)
//	@GeneratedValue
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "id_gen")
//	@SequenceGenerator(name="book_generator", sequenceName = "book_seq", allocationSize=50)
//	@Column(name = "id", updatable = false, nullable = false)

	private Long id;

	@Version
	private int version;

	public Long getId() {
		return id;
	}

	public int getVersion() {
		return version;
	}

	@Override
	public int hashCode() {
		if (id == null) {
			return super.hashCode();
		}

		return 31 + id.hashCode();
	}

	@Override
	public boolean equals(Object other) {
		if (id == null) {
			// New entities are only equal if the instance is the same
			return super.equals(other);
		}

		if (this == other) {
			return true;
		}
		if (!(other instanceof AbstractEntity)) {
			return false;
		}
		return id.equals(((AbstractEntity) other).id);
	}

}
