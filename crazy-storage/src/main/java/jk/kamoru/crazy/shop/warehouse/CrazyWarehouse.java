package jk.kamoru.crazy.shop.warehouse;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import jk.kamoru.crazy.domain.Actress;
import jk.kamoru.crazy.domain.History;
import jk.kamoru.crazy.domain.Image;
import jk.kamoru.crazy.domain.Studio;
import jk.kamoru.crazy.domain.Video;
import jk.kamoru.crazy.shop.purifier.HistoryPurifier;
import jk.kamoru.crazy.shop.purifier.ImagePurifier;
import jk.kamoru.crazy.shop.purifier.VideoPurifier;
import lombok.Getter;

@Getter
@Repository
public class CrazyWarehouse implements Warehouse {

	private Map<String, Studio> studios;
	private Map<String, Video> videos;
	private Map<String, Actress> actresses;
	private List<Image> images;
	private List<History> histories;
	
	@Override
	public void pushVideoPurifier(VideoPurifier videoPurifier) {
		videos = videoPurifier.getVideos();
		studios = videoPurifier.getStudios();
		actresses = videoPurifier.getActresses();
	}

	@Override
	public void pushImagePurifier(ImagePurifier imagePurifier) {
		images = imagePurifier.list();
	}

	@Override
	public void pushHistoryPurifier(HistoryPurifier historyPurifier) {
		histories = historyPurifier.getList();
	}

	@Override
	public Map<String, Video> getVideos() {
		return videos;
	}

	@Override
	public Map<String, Studio> getStudios() {
		return studios;
	}

	@Override
	public Map<String, Actress> getActresses() {
		return actresses;
	}

	@Override
	public List<Image> getImages() {
		return images;
	}

	@Override
	public List<History> getHistories() {
		return histories;
	}


}
