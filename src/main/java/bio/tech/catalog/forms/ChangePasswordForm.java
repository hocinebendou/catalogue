package bio.tech.catalog.forms;

import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.Size;

public class ChangePasswordForm {

	@NotEmpty
	@Size(min = 6, max = 32)
	private String username;

	@NotEmpty
	@Size(min = 6, max = 32)
	private String password;

	@NotEmpty
	private String passwordConfirm;

	// getters
	public String getUsername() {
		return username;
	}
	public String getPassword() {
		return password;
	}
	public String getPasswordConfirm() {
		return passwordConfirm;
	}

	// setters
	public void setUsername(String username) {
		this.username = username;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public void setPasswordConfirm(String passwordConfirm) {
		this.passwordConfirm = passwordConfirm;
	}

	public boolean passwordMatches () {
		return this.getPassword().equals(this.getPasswordConfirm());
	}
}
