package bast1aan.commenter;

import com.opensymphony.xwork2.ActionSupport;

/**
 *
 * @author bastiaan
 */
public class HelloWorldAction extends ActionSupport {

    private static final long serialVersionUID = 1012945817L;
	
	private String message;
	private String username;

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
	
	@Override
	public String execute() throws Exception {
		setMessage(String.format("Hallo allemaal, %s", username));
		return SUCCESS;
	}
	
	public void setUsername(String username) {
		this.username = username;
	}
	
	public String getUsername() {
		return username;
	}
	
}
