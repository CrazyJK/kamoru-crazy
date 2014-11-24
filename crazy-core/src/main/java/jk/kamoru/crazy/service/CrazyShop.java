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
	
	Video getVideo();
	Video getVideo(String opus);
	Studio getStudio(String name);
	Actress getActress(String name);
	
	List<Video> findVideo(Search search);
	List<Studio> findStudio(Search search);
	List<Actress> findActress(Search search);
	
	void feedbackVideo(Video video);
	void feedbackStudio(Studio studio);
	void feedbackActress(Actress actress);
	
	// about Image
	
	Image getImage();
	Image getImage(Integer idx);

	List<Image> findImage(Search search);

	void feedbackImage(Integer idx);
	
	// about History
	
	List<History> findHistory(Search search);
	
}
