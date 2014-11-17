package jk.kamoru.crazy.shop.purifier;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Provider;

import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.factory.annotation.Autowired;

import jk.kamoru.crazy.CRAZY;
import jk.kamoru.crazy.CrazyException;
import jk.kamoru.crazy.domain.Actress;
import jk.kamoru.crazy.domain.Studio;
import jk.kamoru.crazy.domain.Video;
import jk.kamoru.crazy.service.Purifier;
import jk.kamoru.crazy.service.Warehouse;
import jk.kamoru.crazy.shop.holder.ActressHolder;
import jk.kamoru.crazy.shop.holder.StudioHolder;
import jk.kamoru.crazy.shop.holder.VideoHolder;
import jk.kamoru.crazy.util.CrazyUtils;
import jk.kamoru.util.FileUtils;
import jk.kamoru.util.StringUtils;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class VideoPurifier implements Purifier<File> {

	private final String UNKNOWN 			 = "_Unknown";
	private final String unclassifiedStudio  = UNKNOWN;
	private final String unclassifiedOpus 	 = UNKNOWN;

	private Map<String, Video>     videoMap	= new HashMap<String, Video>();
	private Map<String, Studio>   studioMap	= new HashMap<String, Studio>();
	private Map<String, Actress> actressMap = new HashMap<String, Actress>();

	// Domain provider
	@Inject Provider<Video>     videoProvider;
	@Inject Provider<Studio>   studioProvider;
	@Inject Provider<Actress> actressProvider;

	private String 	     videoExtensions;
	private String 	     coverExtensions;
	private String 	 subtitlesExtensions;

	private Collection<File> items;
	
	@Autowired Warehouse warehouse;
	
	@PostConstruct
	private void afterPropertySet() {
		if (videoExtensions == null)
			throw new CrazyException("videoExtensions must be set");
		if (coverExtensions == null)
			throw new CrazyException("videoExtensions must be set");
		if (subtitlesExtensions == null)
			throw new CrazyException("videoExtensions must be set");
	}

	@Override
	public void fromHarvester(Collection<File> items) {
		this.items = items;
		
		purifyAndToWarehouse();
	}

	@Override
	public void purifyAndToWarehouse() {
		int unclassifiedNo = 1;
		for (File file : items) {
			try {
				String filename = file.getName();
				String     name = FileUtils.getNameExceptExtension(file);
				String      ext = FileUtils.getExtension(file).toLowerCase();
				
				// 연속 스페이스 제거
				name = StringUtils.normalizeSpace(name);
				// Unnecessary file exclusion
				if (filename.equals(CRAZY.HISTORY_LOG) 
						|| filename.equals(CRAZY.MAC_NETWORKSTORES)
						|| filename.equals(CRAZY.WINDOW_DESKTOPINI)
						|| ext.equals(CRAZY.EXT_ACTRESS) 
						|| ext.equals(CRAZY.EXT_STUDIO))
					continue;
				
				// 1       2     3      4        5     6
				// [studio][opus][title][actress][date]etc...
				String[] names 		= StringUtils.split(name, "]");
				String studioName  	= UNKNOWN;
				String opus    		= UNKNOWN;
				String title   		= filename;
				String actressNames = UNKNOWN;
				String releaseDate 	= "";
				String etcInfo 		= "";
				
				switch (names.length) {
				case 6:
					etcInfo 	= CrazyUtils.removeUnnecessaryCharacter(names[5]);
				case 5:
					releaseDate = CrazyUtils.removeUnnecessaryCharacter(names[4]);
				case 4:
					actressNames = CrazyUtils.removeUnnecessaryCharacter(names[3], CRAZY.UNCLASSIFIEDACTRESS);
				case 3:
					title 		= CrazyUtils.removeUnnecessaryCharacter(names[2], UNKNOWN);
				case 2:
					opus 		= CrazyUtils.removeUnnecessaryCharacter(names[1], unclassifiedOpus);
					studioName 	= CrazyUtils.removeUnnecessaryCharacter(names[0], unclassifiedStudio);
					break;
				case 1:
					studioName 	= unclassifiedStudio;
					opus 		= unclassifiedOpus + unclassifiedNo++;
					title 		= filename;
					actressNames = CRAZY.UNCLASSIFIEDACTRESS;
					break;
				default: // if names length is over 6
					log.debug("File [{}] [{}] [{}]", filename, names.length, ArrayUtils.toString(names));
					studioName 	= CrazyUtils.removeUnnecessaryCharacter(names[0], unclassifiedStudio);
					opus 		= CrazyUtils.removeUnnecessaryCharacter(names[1], unclassifiedOpus);
					title 		= CrazyUtils.removeUnnecessaryCharacter(names[2], UNKNOWN);
					actressNames = CrazyUtils.removeUnnecessaryCharacter(names[3], CRAZY.UNCLASSIFIEDACTRESS);
					releaseDate = CrazyUtils.removeUnnecessaryCharacter(names[4]);
					for (int i=5, iEnd=names.length; i<iEnd; i++)
						etcInfo = etcInfo + " " + CrazyUtils.removeUnnecessaryCharacter(names[i]);
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
				if (videoExtensions.toLowerCase().contains(ext))
					video.addVideoFile(file);
				else if (coverExtensions.toLowerCase().contains(ext))
					video.setCoverFile(file);
				else if (subtitlesExtensions.toLowerCase().contains(ext))
					video.addSubtitlesFile(file);
				else if (CRAZY.EXT_INFO.equalsIgnoreCase(ext))
					video.setInfoFile(file);
				else if (CRAZY.EXT_WEBP.equalsIgnoreCase(ext))
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
					String forwardActressName = CrazyUtils.forwardNameSort(actressName);
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
			}
			catch (NullPointerException e) {
				log.error("", e);
			}
			catch (Exception e) {
				log.error("Error : {} - {}", file.getAbsolutePath(), e);
			}
		}
		log.info("Total loaded video {}", videoMap.size());
		
		toWarehouse();
	}

	public void toWarehouse() {
		warehouse.fromPurifier(new VideoHolder(videoMap));
		warehouse.fromPurifier(new StudioHolder(studioMap));
		warehouse.fromPurifier(new ActressHolder(actressMap));
	}



}
