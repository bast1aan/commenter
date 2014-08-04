package bast1aan.commenter;

import com.opensymphony.xwork2.ActionSupport;

/**
 *
 * @author bastiaan
 */
public class HelloWorldAction extends ActionSupport {

    private static final long serialVersionUID = 1012945817L;
	
	//public static final String SUCCESS = "success";
	
	private String message;

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
	
	public String execute() throws Exception {
		setMessage("Hallo allemaal");
		return SUCCESS;
	}
	
}
