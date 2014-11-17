package jk.kamoru.crazy.video;


public class VideoNotFoundException extends VideoException {

	private static final long serialVersionUID = VIDEO.SERIAL_VERSION_UID;

	public VideoNotFoundException(String message, Throwable cause) {
		super(message, cause);
	}

	public VideoNotFoundException(String message) {
		super(message);
	}

	public VideoNotFoundException(Throwable cause) {
		super(cause);
	}
	
}
