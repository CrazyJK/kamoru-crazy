package jk.kamoru.crazy.shop.schedule;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import jk.kamoru.crazy.CRAZY;
import jk.kamoru.crazy.domain.Video;
import jk.kamoru.crazy.shop.harvester.Harvester;
import jk.kamoru.crazy.shop.warehouse.Warehouse;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class Scheduling {

	private static final Logger logger = LoggerFactory.getLogger(Scheduling.class);

	@Autowired Harvester harvester;
	@Autowired Warehouse warehouse;

	@Value("${task.move.watchedVideo}") 			private boolean MOVE_WATCHED_VIDEO;
	@Value("${task.delete.lowerRankVideo}") 		private boolean DELETE_LOWER_RANK_VIDEO;
	@Value("${task.delete.lowerScoreVideo}") 		private boolean DELETE_LOWER_SCORE_VIDEO;

	@Value("${rank.baseline}")	private int baselineRank;
	@Value("${size.G.storage.maximum}")	private int maximumStorageGBSize;
	
    @Value("#{'${path.storage.video}'.split(',')}") private List<String> videoStoragePaths;

    private static final long minimumSpace = 10 * FileUtils.ONE_GB;
    
	@Scheduled(cron="0 */5 * * * *")
	public void videoTask() {
		
		synchronized (CRAZY.class) {
			long startTime = System.currentTimeMillis();
			logger.info("BATCH START");

			Collection<Video> videos = warehouse.getVideos().values();

			logger.info("  BATCH : delete lower rank video [{}]", DELETE_LOWER_RANK_VIDEO);
			if (DELETE_LOWER_RANK_VIDEO) {
				for (Video video : videos) {
					if (video.getRank() < baselineRank) {
						videos.remove(video);
						for (File file : video.getFileAll()) {
							FileUtils.deleteQuietly(file);
						}
					}
				}
			}
				
			
			logger.info("  BATCH : delete lower score video [{}]", DELETE_LOWER_SCORE_VIDEO);
			if (DELETE_LOWER_SCORE_VIDEO) {
				Map<Integer, Video> scoreMap = new TreeMap<Integer, Video>(Collections.reverseOrder());
				for (Video video : videos) {
					scoreMap.put(video.getScore(), video);
				}
				long length = 0;
				long maximumStorageSize = maximumStorageGBSize * FileUtils.ONE_GB;
				for (Video video : scoreMap.values()) {
					if (video.getRank() == baselineRank)
						continue;
					length += video.getLength();
					if (length > maximumStorageSize) {
						videos.remove(video);
						for (File file : video.getFileAll()) {
							FileUtils.deleteQuietly(file);
						}
					}
				}
			}

			
			logger.info("  BATCH : arrange to same folder");
			for (Video video : videos) {
				if (video.isExistVideoFileList()) {
					File destination = video.getDelegatePathFile();
					for (File file : video.getFileAll()) {
						if (file == null || !file.isFile())
							continue;
						if (!destination.getPath().equals(file.getParentFile().getPath())) {
							try {
								FileUtils.moveFileToDirectory(file, destination, false);
							} catch (IOException e) {
								logger.warn("video arrange warning : {}", e.getMessage());
							}
						}
					}
				}
			}
			
			logger.info("  BATCH : move watched video [{}]", MOVE_WATCHED_VIDEO);
			if (MOVE_WATCHED_VIDEO) {
				if (videoStoragePaths.size() > 1) {
					File destination = new File(videoStoragePaths.get(0));
					for (Video video : videos) {
						if (!video.getDelegatePath().startsWith(videoStoragePaths.get(0))) {
							long usableSpace = destination.getUsableSpace();
							if (usableSpace > minimumSpace) {
								for (File file : video.getFileAll()) {
									try {
										FileUtils.moveFileToDirectory(file, new File(destination, video.getStudio().getName()), true);
									} catch (IOException e) {
										logger.warn("video arrange warning : {}", e.getMessage());
									}
								}
							}
							else {
								// minimum space exceeded
								break;
							}
						}
					}
				}
			}


			logger.info("  BATCH : reload");
			harvester.videoHarvest();
			
			long elapsedTime = System.currentTimeMillis() - startTime;
			logger.info("BATCH END. Elapsed time : {} ms", elapsedTime);
		}
	}
	
	@Scheduled(cron = "0 */11 * * * *")
	public void imageTask() {
		synchronized (CRAZY.class) {
			harvester.imageHarvest();
		}
	}
	
	@Scheduled(cron = "0 0 */1 * * *")
	public void historyTask() {
		synchronized (CRAZY.class) {
			harvester.historyHarvest();
		}
	}
	
}
