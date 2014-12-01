package jk.kamoru.crazy.video;

import jk.kamoru.crazy.video.service.VideoService;
import lombok.Setter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Setter
public class VideoBatch {

	private static final Logger logger = LoggerFactory.getLogger(VideoBatch.class);

	@Autowired VideoService videoService;

	@Value("#{prop['watched.moveVideo']}") 			private boolean MOVE_WATCHED_VIDEO;
	@Value("#{prop['rank.deleteVideo']}") 			private boolean DELETE_LOWER_RANK_VIDEO;
	@Value("#{prop['score.deleteVideo']}") 			private boolean DELETE_LOWER_SCORE_VIDEO;

	@Scheduled(cron="0 */5 * * * *")
	public void batchVideoSource() {
		
		synchronized (VIDEO.class) {
			long startTime = System.currentTimeMillis();
			logger.info("BATCH START");

			logger.info("  BATCH : delete lower rank video [{}]", DELETE_LOWER_RANK_VIDEO);
			if (DELETE_LOWER_RANK_VIDEO)
				videoService.removeLowerRankVideo();
			
			logger.info("  BATCH : delete lower score video [{}]", DELETE_LOWER_SCORE_VIDEO);
			if (DELETE_LOWER_SCORE_VIDEO)
				videoService.removeLowerScoreVideo();
			
			logger.info("  BATCH : delete garbage file");
			videoService.deleteGarbageFile();
			
			logger.info("  BATCH : arrange to same folder");
			videoService.arrangeVideo();
			
			logger.info("  BATCH : move watched video [{}]", MOVE_WATCHED_VIDEO);
			if (MOVE_WATCHED_VIDEO)
				videoService.moveWatchedVideo();

			logger.info("  BATCH : reload");
			videoService.reload();
			
			long elapsedTime = System.currentTimeMillis() - startTime;
			logger.info("BATCH END. Elapsed time : {} ms", elapsedTime);
		}
	}
	
}
