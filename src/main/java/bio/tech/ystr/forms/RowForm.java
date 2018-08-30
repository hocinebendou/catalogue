package bio.tech.ystr.forms;

import java.util.ArrayList;
import java.util.List;

public class RowForm {

    private String acronym;
    private String design;
    private String disease;
    private String sex;
    private String ethnicity;
    private String country;
    private String specType;
    private int nbSamples = 0;

    private List<String> biobanks = new ArrayList<>();

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

    public String getSpecType() {
        return specType;
    }

    public void setSpecType(String specType) {
        this.specType = specType;
    }

    public int getNbSamples() {
        return nbSamples;
    }

    public void setNbSamples(int nbSamples) {
        this.nbSamples = nbSamples;
    }

    public List<String> getBiobanks() {
        return biobanks;
    }

    public void setBiobanks(List<String> biobanks) {
        this.biobanks = biobanks;
    }
}
