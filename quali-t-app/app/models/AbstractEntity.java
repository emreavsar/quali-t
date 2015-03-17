package models;

import javax.persistence.*;

/**
 * Created by emre on 06/03/15.
 */
@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public abstract class AbstractEntity {

	@SuppressWarnings("UnusedDeclaration") //generated by hibernate
	@Id
	@SequenceGenerator(name = "entity_seq", sequenceName = "entity_seq")
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "entity_seq")
	private Long id;

	public Long getId() {
		return id;
	}

	@Deprecated //only used for hibernate
	public void setId(Long id) {
		this.id = id;
	}

}
