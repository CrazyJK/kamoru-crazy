package jk.kamoru.crazy.video;


public class ActressNotFoundException extends VideoException {

	private static final long serialVersionUID = VIDEO.SERIAL_VERSION_UID;

	public ActressNotFoundException(String message, Throwable cause) {
		super(message, cause);
	}

	public ActressNotFoundException(String message) {
		super(message);
	}

	public ActressNotFoundException(Throwable cause) {
		super(cause);
	}
	
}
