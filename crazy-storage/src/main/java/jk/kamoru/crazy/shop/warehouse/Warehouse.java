package jk.kamoru.crazy.shop.warehouse;

import java.util.List;
import java.util.Map;

import jk.kamoru.crazy.domain.Actress;
import jk.kamoru.crazy.domain.History;
import jk.kamoru.crazy.domain.Image;
import jk.kamoru.crazy.domain.Studio;
import jk.kamoru.crazy.domain.Video;
import jk.kamoru.crazy.shop.purifier.HistoryPurifier;
import jk.kamoru.crazy.shop.purifier.ImagePurifier;
import jk.kamoru.crazy.shop.purifier.VideoPurifier;

/**
 * 창고
 * <p>정제기({@link jk.kamoru.crazy.shop.purifier.Purifier Purifier})가 
 * 만든 상품을 담고 있다가 원하는 곳에 출고한다.<p>
 * @author kamoru
 *
 */
public interface Warehouse {

	void pushVideoPurifier(VideoPurifier videoPurifier);

	void pushImagePurifier(ImagePurifier imagePurifier);

	void pushHistoryPurifier(HistoryPurifier historyPurifier);

	Map<String, Video> getVideos();

	Map<String, Studio> getStudios();

	Map<String, Actress> getActresses();

	List<Image> getImages();

	List<History> getHistories();
	
}
