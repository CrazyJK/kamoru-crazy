package jk.kamoru.crazy.service;

import java.util.List;

import jk.kamoru.crazy.domain.Actress;
import jk.kamoru.crazy.domain.History;
import jk.kamoru.crazy.domain.Image;
import jk.kamoru.crazy.domain.Search;
import jk.kamoru.crazy.domain.Studio;
import jk.kamoru.crazy.domain.Video;

/**
 * 미친 가게
 * <p>비디오도 팔고, 사진도 서비스로 주고, 판매 이력도 막 알려주는...<br>
 * {@link jk.kamoru.crazy.storage.dao.ItemFinder ItemFinder}가 찾아 온다</p>
 * @author kamoru
 *
 */
public interface CrazyShop {

	// about Video
	
	Video video();
	Video video(String opus);
	Studio studio(String name);
	Actress actress(String name);
	
	List<Video> listVideo(Search search);
	List<Studio> listStudio(Search search);
	List<Actress> listActress(Search search);
	
	void feedbackVideo(Video video);
	void feedbackStudio(Studio studio);
	void feedbackActress(Actress actress);
	
	// about Image
	
	Image image();
	Image image(Integer idx);

	List<Image> listImage(Search search);

	void feedbackImage(Integer idx);
	
	// about History
	
	List<History> listHistory(Search search);
	
}
