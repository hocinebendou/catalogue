package bio.tech.catalog.persistence.model;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import org.neo4j.ogm.annotation.GraphId;
import org.neo4j.ogm.annotation.NodeEntity;

@JsonIdentityInfo(generator=ObjectIdGenerators.PropertyGenerator.class, property="id")
@NodeEntity
public class NeoGender {
	
	@GraphId
	private Long id;

	private String name;
	
	public NeoGender() {}

	public NeoGender(String name) {
		this.name = name;
	}

	public String getName() {
	    return name;
	}
}
