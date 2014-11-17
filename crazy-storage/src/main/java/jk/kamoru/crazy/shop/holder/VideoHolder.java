package jk.kamoru.crazy.shop.holder;

import java.util.Map;

import jk.kamoru.crazy.domain.Video;
import jk.kamoru.crazy.service.ItemHolder;

public class VideoHolder implements ItemHolder<Map<String, Video>> {

	Map<String, Video> item;
	
	public VideoHolder(Map<String, Video> item) {
		super();
		this.item = item;
	}

	@Override
	public void setItem(Map<String, Video> item) {
		this.item = item;
		
	}

	@Override
	public Map<String, Video> getItem() {
		return item;
	}


}
