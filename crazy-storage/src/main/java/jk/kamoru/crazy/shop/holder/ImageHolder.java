package jk.kamoru.crazy.shop.holder;

import java.util.List;

import jk.kamoru.crazy.domain.Image;
import jk.kamoru.crazy.service.ItemHolder;

public class ImageHolder implements ItemHolder<List<Image>> {

	List<Image> item;
	
	public ImageHolder(List<Image> item) {
		super();
		this.item = item;
	}

	@Override
	public void setItem(List<Image> item) {
		this.item = item;
	}

	@Override
	public List<Image> getItem() {
		return item;
	}

}
