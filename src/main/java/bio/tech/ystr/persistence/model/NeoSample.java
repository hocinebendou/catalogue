package bio.tech.ystr.persistence.model;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import org.neo4j.ogm.annotation.GraphId;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

// TODO: DELETE IT LATER. WE USE NEOSPECIMEN INSTEAD

@JsonIdentityInfo(generator=ObjectIdGenerators.PropertyGenerator.class, property="id")
@NodeEntity
public class NeoSample {
	
	@GraphId
    private Long id;
	
	private String sampleId;
	private String participantId;
	private String study;
	private String collectionDate;
	private String ageAtCollection;
	private String sampleVolume;
	private String dnaConcentration;
	private String dnaPurity_260_280;
	private String extractionMethod;
	private String sampleAvailable;
	private String biobankName;
	
	@Relationship(type="HAS_CHARACTER", direction=Relationship.OUTGOING)
    private NeoCharacter character = new NeoCharacter();

    @Relationship(type="HAS_GENDER", direction=Relationship.OUTGOING)
    private NeoGender gender = new NeoGender();
    
    @Relationship(type="HAS_COUNTRY", direction=Relationship.OUTGOING)
    private NeoCountry country = new NeoCountry();
    
    @Relationship(type="HAS_SPECTYPE", direction=Relationship.OUTGOING)
    private NeoSpecType specType = new NeoSpecType();
    
	public NeoSample() {}

	// getters
	
	public Long getId() {
		return id;
	}
	
	public String getSampleId() {
		return sampleId;
	}

	public String getParticipantId() {
		return participantId;
	}

	public String getStudy() {
		return study;
	}

	public String getCollectionDate() {
		return collectionDate;
	}

	public String getAgeAtCollection() {
		return ageAtCollection;
	}

	public String getSampleVolume() {
		return sampleVolume;
	}
	
	public String getDnaConcentration() {
		return dnaConcentration;
	}

	public String getDnaPurity_260_280() {
		return dnaPurity_260_280;
	}

	public String getExtractionMethod() {
		return extractionMethod;
	}
	
	public String getSampleAvailable() {
		return sampleAvailable;
	}
	
	public String getBiobankName() {
		return biobankName;
	}
	
	public NeoCharacter getCharacter() {
		return character;
	}
	
	public NeoCountry getCountry() {
		return country;
	}
	
	public NeoGender getGender() {
		return gender;
	}
	
	public NeoSpecType getSpecType() {
		return specType;
	}
	
	// setters
	public void setSampleAvailable(String available) {
		this.sampleAvailable = available;
	}
	
}
