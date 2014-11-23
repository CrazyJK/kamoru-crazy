package jk.kamoru.crazy;


import jk.kamoru.KAMORU;
import jk.kamoru.crazy.domain.SortVideo;

public interface CRAZY extends KAMORU {
	
	public static final String CHARSET = "UTF-8";
	
	public static final String URL_ENCODING = CHARSET;

	public static final String FILE_ENCODING = CHARSET;

	public static final long WEBCACHETIME_SEC = 86400*7l;

	public static final long WEBCACHETIME_MILI = WEBCACHETIME_SEC*1000l;

	public static final SortVideo DEFAULT_SORTMETHOD = SortVideo.T;

	public static final String HISTORY_LOG = "history.log";

	public static final String MAC_NETWORKSTORES = ".DS_Store";

	public static final String WINDOW_DESKTOPINI = "desktop.ini";
	
	public static final String EXT_ACTRESS = "actress";

	public static final String EXT_STUDIO = "studio";

	public static final String EXT_INFO = "info";

	public static final String EXT_WEBP = "webp";
	
	public static final String VIDEO_DATE_PATTERN = "yyyy-MM-dd HH:mm:ss";

	public static final String UNCLASSIFIEDACTRESS = "Amateur";

	public static final int RMI_PORT_BASE = 18588;

	public static final int RMI_PORT_STORAGE = RMI_PORT_BASE;

	public static final String VIDEO_FILTER = "avi,mpg,mkv,wmv,mp4,mov,rmvb";
	
	public static final String COVER_FILTER = "jpg,jpeg,png";
	
	public static final String SUBTITES_FILTER = "smi,srt,ass";

	public static final String IMAGE_FILTER = "jpg,jpeg,gif,png";

}
