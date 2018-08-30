package bio.tech.ystr.persistence.model;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import org.neo4j.ogm.annotation.GraphId;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

import java.util.ArrayList;
import java.util.List;

@JsonIdentityInfo(generator=ObjectIdGenerators.PropertyGenerator.class, property="id")
@NodeEntity
public class NeoParticipant {
    @GraphId
    private Long id;

    private String participantId;
    private String acronym;
    private int age;
    private int height;
    private float weight;
    private float bmi;
    private int noSpecimens;

    @Relationship(type="HAS_GENDER")
    private NeoGender gender = new NeoGender();

    @Relationship(type="HAS_ETHNICITY")
    private NeoEthnicity ethnicity = new NeoEthnicity();

    @Relationship(type = "HAS_SPECIMEN")
    private List<NeoSpecimen> specimens = new ArrayList<>();

    public NeoParticipant() {}

    public NeoParticipant(String participantId, String acronym, int age, int height) {
        this.participantId = participantId;
        this.acronym = acronym;
        this.age = age;
        this.height = height;
        this.weight = 0;
        this.bmi = 0;
        this.noSpecimens = 0;
    }

    //getters

    public Long getId() {
        return id;
    }
    public String getParticipantId() {
        return participantId;
    }
    public String getAcronym() {
        return acronym;
    }
    public int getAge() { return age; }
    public int getHeight() { return height; }
    public float getWeight() { return weight; }
    public float getBmi() { return bmi; }
    public int getNoSpecimens() { return noSpecimens; }
    public NeoGender getGender() {
        return gender;
    }
    public NeoEthnicity getEthnicity() { return ethnicity; }
    public List<NeoSpecimen> getSpecimens() {
        return specimens;
    }

    // setters

    public void setAge(int age) { this.age = age; }
    public void setHeight(int height) { this.height = height; }
    public void setWeight(float weight) { this.weight = weight; }
    public void setBmi(float bmi) { this.bmi = bmi; }
    public void setNoSpecimens(int noSpecimens) { this.noSpecimens= noSpecimens; }
    public void setGender(NeoGender gender) { this.gender = gender; }
    public void addSpecimen(NeoSpecimen specimen) {
        this.specimens.add(specimen);
    }
    public void setAcronym(String acronym) { this.acronym = acronym; }
}
