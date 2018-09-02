package bio.tech.catalog.forms;

import bio.tech.catalog.persistence.model.*;

import java.util.Collection;

public class SearchForm {

	private String studyName;
	private String disease;
    private String descriptionKeys;
	private String design;
    private String country;
    private String gender;
    private String specType;
    private String character;
    private String ethnicity;
    private boolean hasSpecimens;
	private boolean hasDataSets;

	private String bmiOp;
	private String bmiVal;
	private String ageOp;
	private String ageVal;

	private Collection<String> diseases;
	private Collection<NeoDesign> designs;
	private Collection<NeoCountry> countries;
    private Collection<NeoGender> genders;
    private Collection<NeoSpecType> specTypes;
    private Collection<NeoCharacter> characters;
    private Collection<NeoEthnicity> ethnicities;

	// Getters and Setters
	
	public String getStudyName() {
		return studyName;
	}
	public String getDisease() {
		return disease;
	}
    public String getDescriptionKeys() {
        return descriptionKeys;               
    }
	public String getDesign() {
		return design;
	}
	public boolean getHasSpecimens() {
		return hasSpecimens;
	}
	public boolean getHasDataSets() { return hasDataSets; }
    public String getCountry() {
        return country;
    }
    public String getGender() { return gender; }
    public String getSpecType() { return specType; }
    public String getCharacter() { return character; }
    public String getEthnicity() { return ethnicity; }
    public String getBmiOp() { return bmiOp; }
    public String getBmiVal() { return bmiVal; }
    public String getAgeOp() { return ageOp; }
    public String getAgeVal() { return ageVal; }

	public void setStudyName(String studyName) {
		this.studyName = studyName;
	}
	public void setDisease(String disease) {
		this.disease = disease;
	}
    public void setDescriptionKeys(String descriptionKeys) {
        this.descriptionKeys = descriptionKeys;               
    }
	public void setDesign(String design) {
		this.design = design;
	}
	public void setHasSpecimens(boolean hasSpecimens) {
		this.hasSpecimens = hasSpecimens;
	}
	public void setHasDataSets(boolean hasDataSets) { this.hasDataSets = hasDataSets; }
    public void setCountry(String country) {
        this.country = country;
    }
    public void setGender(String gender) { this.gender = gender; }
    public void setSpecType(String specType) { this.specType = specType; }
    public void setCharacter(String character) { this.character = character; }
    public void setEthnicity(String ethnicity) { this.ethnicity = ethnicity; }
    public void setBmiOp(String bmiOp) { this.bmiOp = bmiOp; }
    public void setBmiVal(String bmiVal) { this.bmiVal = bmiVal; }
    public void setAgeOp(String ageOp) { this.ageOp = ageOp; }
    public void setAgeVal(String ageVal) { this.ageVal = ageVal; }

	public Collection<String> getDiseases() { return diseases; }
	public Collection<NeoDesign> getDesigns() { return designs; }
	public Collection<NeoCountry> getCountries() {
		return countries;
	}
	public Collection<NeoGender> getGenders() { return genders; }
	public Collection<NeoSpecType> getSpecTypes() { return specTypes; }
	public Collection<NeoCharacter> getCharacters() { return characters; }
	public Collection<NeoEthnicity> getEthnicities() { return ethnicities; }

	public void setDiseases(Collection<String> diseases) {
	    this.diseases = diseases;
    }
	public void setDesigns(Collection<NeoDesign> designs) {
	    this.designs = designs;
    }
	public void setCountries(Collection<NeoCountry> countries) {
		this.countries = countries;
	}
	public void setGenders(Collection<NeoGender> genders) {
	    this.genders = genders;
    }
    public void setSpecTypes(Collection<NeoSpecType> specTypes) {
	    this.specTypes = specTypes;
    }
    public void setCharacters(Collection<NeoCharacter> characters) {
	    this.characters = characters;
    }
    public void setEthnicities(Collection<NeoEthnicity> ethnicities) {
	    this.ethnicities = ethnicities;
    }
}
