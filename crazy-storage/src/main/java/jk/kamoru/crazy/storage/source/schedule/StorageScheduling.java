package jk.kamoru.crazy.storage.source.schedule;

import jk.kamoru.crazy.CRAZY;
import jk.kamoru.crazy.storage.source.ImageSource;
import jk.kamoru.crazy.storage.source.VideoSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class StorageScheduling {

	private static final Logger logger = LoggerFactory.getLogger(StorageScheduling.class);

	@Autowired VideoSource videoSource;
	@Autowired ImageSource imageSource;

	@Value("${task.move.watchedVideo}") 			private boolean MOVE_WATCHED_VIDEO;
	@Value("${task.delete.lowerRankVideo}") 		private boolean DELETE_LOWER_RANK_VIDEO;
	@Value("${task.delete.lowerScoreVideo}") 		private boolean DELETE_LOWER_SCORE_VIDEO;

	@Scheduled(cron="0 */5 * * * *")
	public void videoTask() {
		
		synchronized (CRAZY.class) {
			long startTime = System.currentTimeMillis();
			logger.info("BATCH START");

			logger.info("  BATCH : delete lower rank video [{}]", DELETE_LOWER_RANK_VIDEO);
			if (DELETE_LOWER_RANK_VIDEO)
				// TODO
//				videoSource.removeLowerRankVideo();
			
			logger.info("  BATCH : delete lower score video [{}]", DELETE_LOWER_SCORE_VIDEO);
			if (DELETE_LOWER_SCORE_VIDEO)
				// TODO
//				videoSource.removeLowerScoreVideo();
			
			logger.info("  BATCH : delete garbage file");
			// TODO
//			videoSource.deleteGarbageFile();
			
			logger.info("  BATCH : arrange to same folder");
			// TODO
//			videoSource.arrangeVideo();
			
			logger.info("  BATCH : move watched video [{}]", MOVE_WATCHED_VIDEO);
			if (MOVE_WATCHED_VIDEO)
				// TODO
//				videoSource.moveWatchedVideo();

			logger.info("  BATCH : reload");
			videoSource.reload();
			
			long elapsedTime = System.currentTimeMillis() - startTime;
			logger.info("BATCH END. Elapsed time : {} ms", elapsedTime);
		}
	}
	
	@Scheduled(cron = "0 */7 * * * *")
	public void imageTask() {
		synchronized (CRAZY.class) {
			imageSource.reload();
		}
	}
	
}
