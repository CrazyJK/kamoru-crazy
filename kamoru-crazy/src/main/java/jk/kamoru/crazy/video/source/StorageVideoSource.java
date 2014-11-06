package jk.kamoru.crazy.video.source;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Provider;

import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import jk.kamoru.crazy.video.VIDEO;
import jk.kamoru.crazy.video.domain.Actress;
import jk.kamoru.crazy.video.domain.Studio;
import jk.kamoru.crazy.video.domain.Video;
import jk.kamoru.crazy.video.source.storage.VideoStorageFacilities;
import jk.kamoru.crazy.video.util.VideoUtils;
import jk.kamoru.util.FileUtils;
import jk.kamoru.util.StringUtils;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class StorageVideoSource implements VideoSource {

	private final String UNKNOWN = "_Unknown";
	private final String unclassifiedStudio = UNKNOWN;
	private final String unclassifiedOpus = UNKNOWN;
	public static final String unclassifiedActress = "Amateur";

	@Autowired
	private VideoStorageFacilities videoFacilities;
	@Autowired
	private VideoStorageFacilities actressFacilities;
	@Autowired
	private VideoStorageFacilities studioFacilities;

	// Domain provider
	@Inject
	Provider<Video> videoProvider;
	@Inject
	Provider<Studio> studioProvider;
	@Inject
	Provider<Actress> actressProvider;

	// data source
	private Map<String, Video> videoMap = new HashMap<String, Video>();
	private Map<String, Studio> studioMap = new HashMap<String, Studio>();
	private Map<String, Actress> actressMap = new HashMap<String, Actress>();

	// property
	private String[] paths;
	private String video_extensions;
	private String cover_extensions;
	private String subtitles_extensions;
	private boolean webp_mode;
	@SuppressWarnings("unused")
	private String webp_exec;

	// logic variables
	@SuppressWarnings("unused")
	private boolean loaded = false;

	// property setter
	public void setPaths(String[] paths) {
		Assert.notNull(paths, "base paths must not be null");
		this.paths = paths;
	}

	public void setVideo_extensions(String video_extensions) {
		Assert.notNull(video_extensions, "video ext must not be null");
		this.video_extensions = video_extensions;
	}

	public void setCover_extensions(String cover_extensions) {
		Assert.notNull(cover_extensions, "cover ext must not be null");
		this.cover_extensions = cover_extensions;
	}

	public void setSubtitles_extensions(String subtitles_extensions) {
		Assert.notNull(subtitles_extensions, "subtitles ext must not be null");
		this.subtitles_extensions = subtitles_extensions;
	}

	public void setWebp_mode(boolean webp_mode) {
		this.webp_mode = webp_mode;
	}

	public void setWebp_exec(String webp_exec) {
		if (webp_mode)
			Assert.notNull(webp_exec, "Webp mode is true. webp_exec must not be null");
		this.webp_exec = webp_exec;
	}

	protected void init() {
		videoFacilities.create("MAIN", 500 * FileUtils.ONE_GB);
		videoFacilities.create("SUB");
		actressFacilities.create("MAIN");
		studioFacilities.create("Main");
	}

	/**
	 * video데이터를 로드한다.
	 */
	protected synchronized void load() {
		log.trace("load");

		// 1. data source initialize
		// videoMap = new HashMap<String, Video>();
		// studioMap = new HashMap<String, Studio>();
		// actressMap = new HashMap<String, Actress>();
		// videoMap.clear();
		// studioMap.clear();
		// actressMap.clear();

		// 2. file find
		Collection<File> files = new ArrayList<File>();
		for (String path : paths) {
			File directory = new File(path);
			log.debug("directory scanning : {}", directory.getAbsolutePath());
			if (directory.isDirectory()) {
				Collection<File> found = FileUtils.listFiles(directory, null, true);
				log.debug("\tfound file size is {}", found.size());
				files.addAll(found);
			} else {
				log.warn("\tIt is not directory. Pass!!!");
			}
		}
		log.info("total found file size : {}", files.size());

		// 3. domain create & data source
		int unclassifiedNo = 1;
		for (File file : files) {
			try {
				String filename = file.getName();
				String name = FileUtils.getNameExceptExtension(file);
				String ext = FileUtils.getExtension(file).toLowerCase();

				// 연속 스페이스 제거
				name = StringUtils.normalizeSpace(name);
				// Unnecessary file exclusion
				if (filename.equals(VIDEO.HISTORY_LOG) || filename.equals(VIDEO.MAC_NETWORKSTORES) || filename.equals(VIDEO.WINDOW_DESKTOPINI)
						|| ext.equals(VIDEO.EXT_ACTRESS) || ext.equals(VIDEO.EXT_STUDIO))
					continue;

				// 1 2 3 4 5 6
				// [studio][opus][title][actress][date]etc...
				String[] names = StringUtils.split(name, "]");
				String studioName = UNKNOWN;
				String opus = UNKNOWN;
				String title = filename;
				String actressNames = UNKNOWN;
				String releaseDate = "";
				String etcInfo = "";

				switch (names.length) {
				case 6:
					etcInfo = VideoUtils.removeUnnecessaryCharacter(names[5]);
				case 5:
					releaseDate = VideoUtils.removeUnnecessaryCharacter(names[4]);
				case 4:
					actressNames = VideoUtils.removeUnnecessaryCharacter(names[3], unclassifiedActress);
				case 3:
					title = VideoUtils.removeUnnecessaryCharacter(names[2], UNKNOWN);
				case 2:
					opus = VideoUtils.removeUnnecessaryCharacter(names[1], unclassifiedOpus);
					studioName = VideoUtils.removeUnnecessaryCharacter(names[0], unclassifiedStudio);
					break;
				case 1:
					studioName = unclassifiedStudio;
					opus = unclassifiedOpus + unclassifiedNo++;
					title = filename;
					actressNames = unclassifiedActress;
					break;
				default: // if names length is over 6
					log.debug("File [{}] [{}] [{}]", filename, names.length, ArrayUtils.toString(names));
					studioName = VideoUtils.removeUnnecessaryCharacter(names[0], unclassifiedStudio);
					opus = VideoUtils.removeUnnecessaryCharacter(names[1], unclassifiedOpus);
					title = VideoUtils.removeUnnecessaryCharacter(names[2], UNKNOWN);
					actressNames = VideoUtils.removeUnnecessaryCharacter(names[3], unclassifiedActress);
					releaseDate = VideoUtils.removeUnnecessaryCharacter(names[4]);
					for (int i = 5, iEnd = names.length; i < iEnd; i++)
						etcInfo = etcInfo + " " + VideoUtils.removeUnnecessaryCharacter(names[i]);
				}
				Video video = videoMap.get(opus.toLowerCase());
				if (video == null) {
					video = this.videoProvider.get();
					video.setOpus(opus.toUpperCase());
					video.setTitle(title);
					video.setReleaseDate(releaseDate);
					video.setEtcInfo(etcInfo);
					videoMap.put(opus.toLowerCase(), video);
					log.trace("add video - {}", video);
				}
				// set video File
				if (video_extensions.toLowerCase().contains(ext))
					video.addVideoFile(file);
				else if (cover_extensions.toLowerCase().contains(ext)) {
					if (webp_mode)
						// video.setCoverWebpFile(convertWebpFile(file));
						video.setCoverFile(file);
				} else if (subtitles_extensions.toLowerCase().contains(ext))
					video.addSubtitlesFile(file);
				else if (VIDEO.EXT_INFO.equalsIgnoreCase(ext))
					video.setInfoFile(file);
				else if (VIDEO.EXT_WEBP.equalsIgnoreCase(ext))
					video.setCoverWebpFile(file);
				else
					video.addEtcFile(file);

				Studio studio = studioMap.get(studioName.toLowerCase());
				if (studio == null) {
					studio = this.studioProvider.get();
					studio.setName(studioName);
					studioMap.put(studioName.toLowerCase(), studio);
					log.trace("add studio - {}", studio);
				}

				// inject reference
				studio.addVideo(video);
				video.setStudio(studio);

				for (String actressName : StringUtils.split(actressNames, ",")) {
					String forwardActressName = VideoUtils.forwardNameSort(actressName);
					Actress actress = actressMap.get(forwardActressName);
					if (actress == null) {
						actress = actressProvider.get();
						actress.setName(actressName.trim());

						actressMap.put(forwardActressName, actress);
						log.trace("add actress - {}", actress);
					}
					// inject reference
					actress.addVideo(video);
					actress.addStudio(studio);

					studio.addActress(actress);
					video.addActress(actress);
				}
			} catch (NullPointerException e) {
				log.error("", e);
			} catch (Exception e) {
				log.error("Error : {} - {}", file.getAbsolutePath(), e);
			}
		}
		loaded = true;
		log.info("Total loaded video size : {}", videoMap.size());
	}

	@Override
	public Map<String, Video> getVideoMap() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<String, Studio> getStudioMap() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<String, Actress> getActressMap() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Video> getVideoList() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Studio> getStudioList() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Actress> getActressList() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void reload() {
		// TODO Auto-generated method stub

	}

	@Override
	public void removeVideo(String opus) {
		// TODO Auto-generated method stub

	}

	@Override
	public Video getVideo(String opus) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Studio getStudio(String name) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Actress getActress(String name) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void moveVideo(String opus, String destPath) {
		// TODO Auto-generated method stub

	}

	@Override
	public void arrangeVideo(String opus) {
		// TODO Auto-generated method stub

	}

}
