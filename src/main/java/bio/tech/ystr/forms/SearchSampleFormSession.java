package bio.tech.ystr.forms;

import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;

@Component
@Scope(value = "session", proxyMode = ScopedProxyMode.TARGET_CLASS)
public class SearchSampleFormSession {

	private String country;
	private String gender;
	private String specType;
	private String character;
	SearchSampleForm form;
	
	public void saveForm(SearchSampleForm form) {
		this.country = form.getCountry();
		this.gender = form.getGender();
		this.specType = form.getSpecType();
		this.character = form.getCharacter();
	}
	
	public SearchSampleForm toForm() {
		form = new SearchSampleForm();
		form.setCountry(country);
		form.setGender(gender);
		form.setSpecType(specType);
		form.setCharacter(character);
		return form;
	}
	
	public void clearForm() {
		if (form != null) {
			form.setCountry("");
			form.setGender("");
			form.setSpecType("");
			form.setCharacter("");
		}
	}
}
