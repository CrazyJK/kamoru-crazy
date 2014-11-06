package jk.kamoru.crazy.video;

import jk.kamoru.crazy.CRAZY;
import jk.kamoru.crazy.video.domain.Sort;

public interface VIDEO extends CRAZY {
	
	public static final long WEBCACHETIME_SEC = 86400*7l;

	public static final long WEBCACHETIME_MILI = WEBCACHETIME_SEC*1000l;

	public static final Sort DEFAULT_SORTMETHOD = Sort.T;

	public static final String HISTORY_LOG = "history.log";

	public static final String MAC_NETWORKSTORES = ".DS_Store";

	public static final String WINDOW_DESKTOPINI = "desktop.ini";
	
	public static final String EXT_ACTRESS = "actress";

	public static final String EXT_STUDIO = "studio";

	public static final String EXT_INFO = "info";

	public static final String EXT_WEBP = "webp";
	
	public static final String VIDEO_DATE_PATTERN = "yyyy-MM-dd HH:mm:ss";

}
