package bio.tech.catalog.forms;

import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;

@Component
@Scope(value = "session", proxyMode = ScopedProxyMode.TARGET_CLASS)
public class SearchFormSession {

	private String studyName;
	private String disease;
    private String descriptionKeys;
	private String design;
	private boolean hasSpecimens;
	private boolean hasDataSets;
	private String country;
	private String gender;
	private String specType;
	private String character;
	private String ethnicity;
	private String bmiOp;
	private String bmiVal;
	private String ageOp;
	private String ageVal;

	SearchForm searchForm;
	
	public void saveForm(SearchForm searchForm) {
		this.studyName = searchForm.getStudyName();
		this.disease = searchForm.getDisease();
        this.descriptionKeys = searchForm.getDescriptionKeys();
		this.design = searchForm.getDesign();
		this.hasSpecimens = searchForm.getHasSpecimens();
		this.hasDataSets = searchForm.getHasDataSets();
		this.country = searchForm.getCountry();
		this.gender = searchForm.getGender();
		this.specType = searchForm.getSpecType();
		this.character = searchForm.getCharacter();
		this.ethnicity = searchForm.getEthnicity();
		this.bmiOp = searchForm.getBmiOp();
		this.bmiVal = searchForm.getBmiVal();
		this.ageOp = searchForm.getAgeOp();
		this.ageVal = searchForm.getAgeVal();
	}
	
	public SearchForm toForm() {
		searchForm = new SearchForm();
		searchForm.setStudyName(studyName);
		searchForm.setDisease(disease);
        searchForm.setDescriptionKeys(descriptionKeys);
		searchForm.setDesign(design);
		searchForm.setHasSpecimens(hasSpecimens);
		searchForm.setHasDataSets(hasDataSets);
		searchForm.setCountry(country);
		searchForm.setGender(gender);
		searchForm.setSpecType(specType);
		searchForm.setCharacter(character);
		searchForm.setEthnicity(ethnicity);
		searchForm.setBmiOp(bmiOp);
		searchForm.setBmiVal(bmiVal);
		searchForm.setAgeOp(ageOp);
		searchForm.setAgeVal(ageVal);

		return searchForm;
	}
	
	public void clearForm() {
		if (searchForm != null) {
			searchForm.setStudyName("");
			searchForm.setDisease("");
            searchForm.setDescriptionKeys("");
			searchForm.setDesign("");
			searchForm.setHasSpecimens(false);
			searchForm.setHasDataSets(false);
			searchForm.setCountry("");
			searchForm.setGender("");
			searchForm.setSpecType("");
			searchForm.setCharacter("");
			searchForm.setEthnicity("");
            searchForm.setBmiOp("");
            searchForm.setBmiVal("");
            searchForm.setAgeOp("");
            searchForm.setAgeVal("");
		}
	}
}
