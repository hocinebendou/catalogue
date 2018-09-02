package bio.tech.catalog.forms;

import org.hibernate.validator.constraints.NotEmpty;

/**
 * Created by hocine on 2017/04/14.
 */
public class LoginForm {

    @NotEmpty
    private String userName;
    
    @NotEmpty
    private String password;
    
    // getters
    public String getUserName() {
        return userName;
    }
    public String getPassword() {
        return password;
    }
	
	// setters
    public void setUserName(String userName) {
        this.userName = userName;
    }
	public void setPassword(String password) {
        this.password = password;
    }
}
