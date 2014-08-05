package bast1aan.commenter;

public class CommenterException extends RuntimeException {
	public CommenterException(String message) {
		super(message);
	}
	
	public CommenterException(String message, Throwable cause) {
		super(message, cause);
	}
}
