package jk.kamoru.crazy.shop.holder;

import java.util.Map;

import jk.kamoru.crazy.domain.Studio;
import jk.kamoru.crazy.service.ItemHolder;

public class StudioHolder implements ItemHolder<Map<String, Studio>> {

	Map<String, Studio> item;
	
	public StudioHolder(Map<String, Studio> item) {
		super();
		this.item = item;
	}

	@Override
	public void setItem(Map<String, Studio> item) {
		this.item = item;
	}

	@Override
	public Map<String, Studio> getItem() {
		return item;
	}

}
