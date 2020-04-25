package eu.japtor.vizman.backend.entity;

import javax.persistence.*;

@MappedSuperclass
public class AbstractGenIdEntity extends AbstractEntity {

//	@GeneratedValue
//	@SequenceGenerator(name="book_generator", sequenceName = "book_seq", allocationSize=50)
//	@Column(name = "id", updatable = false, nullable = false)

	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "id_gen")
	@SequenceGenerator(
			name="id_gen",
			sequenceName = "vizman_global_seq",
			initialValue = 1000001,
			allocationSize=10	// Looks like it must not be greater than INCREMENT BY specified for sequence in DB SQL
	)

	@Id
	@Column(name = "id", updatable = false, nullable = false)
	private Long id;

	@Version
	private Integer version;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}


	public Integer getVersion() {
		return version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}


	@Override
	public int hashCode() {
		return 31;
//		if (id == null) {
//			return super.hashCode();
//		}
//		return 31 + id.hashCode();
	}

	@Override
	public boolean equals(Object other) {
		if (this == other) return true;
		if (!(other instanceof AbstractGenIdEntity)) return false;
		return id != null && id.equals(((AbstractGenIdEntity) other).id);
//		if (id == null) {
//			// New entities are only equal if the instance is the same
//			return super.equals(other);
//		}
	}
}
