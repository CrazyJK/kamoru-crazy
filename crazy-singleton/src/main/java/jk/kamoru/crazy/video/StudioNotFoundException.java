package jk.kamoru.crazy.video;


public class StudioNotFoundException extends VideoException {

	private static final long serialVersionUID = VIDEO.SERIAL_VERSION_UID;

	public StudioNotFoundException(String message, Throwable cause) {
		super(message, cause);
	}

	public StudioNotFoundException(String message) {
		super(message);
	}

	public StudioNotFoundException(Throwable cause) {
		super(cause);
	}

}
