package bio.tech.ystr.persistence.model;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import org.neo4j.ogm.annotation.GraphId;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

@JsonIdentityInfo(generator=ObjectIdGenerators.PropertyGenerator.class, property="id")
@NodeEntity
public class NeoSpecimen {

    @GraphId
    private Long id;
    private String acronym;
    private String sampleId;
    private String collectionDate;
    private String biobankName;
    private int noAliquots;
    private String ethnicity;
    private String disease;
    private String sex;

    @Relationship(type="HAS_COUNTRY")
    private NeoCountry country = new NeoCountry();

    @Relationship(type="HAS_SPECTYPE")
    private NeoSpecType specType = new NeoSpecType();

    public NeoSpecimen() {}

    public NeoSpecimen(String sampleId, int noAliquots, String collectionDate, String biobankName) {
        this.sampleId = sampleId;
        this.noAliquots = noAliquots;
        this.collectionDate = collectionDate;
        this.biobankName = biobankName;
    }

    // getters
    public Long getId() {
        return id;
    }
    public String getSampleId() {
        return sampleId;
    }
    public String getCollectionDate() {
        return collectionDate;
    }
    public int getNoAliquots() { return noAliquots; }
    public String getBiobankName() {
        return biobankName;
    }
    public NeoCountry getCountry() {
        return country;
    }
    public NeoSpecType getSpecType() {
        return specType;
    }

    public String getAcronym() { return acronym; }
    public String getEthnicity() { return ethnicity; }
    public String getDisease() { return disease; }
    public String getSex() { return sex; }

    // setters
    public void setSampleId(String sampleId) { this.sampleId = sampleId; }
    public void setNoAliquots (int noAliquots) { this.noAliquots = noAliquots; }
    public void setCollectionDate (String collectionDate) { this.collectionDate = collectionDate; }
    public void setBiobankName (String biobankName) { this.biobankName = biobankName; }
    public void setCountry(NeoCountry country) {
        this.country = country;
    }
    public void setSpecType(NeoSpecType specType) {
        this.specType = specType;
    }

    public void setAcronym(String acronym) { this.acronym = acronym; }
    public void setEthnicity(String ethnicity) { this.ethnicity = ethnicity; }
    public void setDisease(String disease) {
        this.disease = disease;
    }
    public void setSex(String sex) { this.sex = sex; }
}