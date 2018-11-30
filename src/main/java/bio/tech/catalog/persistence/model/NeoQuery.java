package bio.tech.catalog.persistence.model;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import org.neo4j.ogm.annotation.GraphId;
import org.neo4j.ogm.annotation.NodeEntity;

@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
@NodeEntity
public class NeoQuery {

    @GraphId
    private Long id;

    private String acronym;
    private String design;
    private String disease;
    private String sex;
    private String ethnicity;
    private String country;
    private String specimenType;
    private String nbRequest;

    private boolean smoking;
    private boolean diet;
    private boolean hivStatus;
    private boolean bloodPressure;
    private boolean alcoholUse;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAcronym() {
        return acronym;
    }

    public void setAcronym(String acronym) {
        this.acronym = acronym;
    }

    public String getDesign() {
        return design;
    }

    public void setDesign(String design) {
        this.design = design;
    }

    public String getDisease() {
        return disease;
    }

    public void setDisease(String disease) {
        this.disease = disease;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getEthnicity() {
        return ethnicity;
    }

    public void setEthnicity(String ethnicity) {
        this.ethnicity = ethnicity;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getSpecimenType() {
        return specimenType;
    }

    public void setSpecimenType(String specimenType) {
        this.specimenType = specimenType;
    }

    public boolean isSmoking() {
        return smoking;
    }

    public void setSmoking(boolean smoking) {
        this.smoking = smoking;
    }

    public boolean isDiet() {
        return diet;
    }

    public void setDiet(boolean diet) {
        this.diet = diet;
    }

    public boolean isHivStatus() {
        return hivStatus;
    }

    public void setHivStatus(boolean hivStatus) {
        this.hivStatus = hivStatus;
    }

    public boolean isBloodPressure() {
        return bloodPressure;
    }

    public void setBloodPressure(boolean bloodPressure) {
        this.bloodPressure = bloodPressure;
    }

    public boolean isAlcoholUse() {
        return alcoholUse;
    }

    public void setAlcoholUse(boolean alcoholUse) {
        this.alcoholUse = alcoholUse;
    }

    public String getNbRequest() {
        return nbRequest;
    }

    public void setNbRequest(String nbRequest) {
        this.nbRequest = nbRequest;
    }
}
