package jk.kamoru.crazy.shop.holder;

import java.util.Map;

import jk.kamoru.crazy.domain.Actress;
import jk.kamoru.crazy.service.ItemHolder;

public class ActressHolder implements ItemHolder<Map<String, Actress>> {

	Map<String, Actress> item;
	
	public ActressHolder(Map<String, Actress> item) {
		super();
		this.item = item;
	}

	@Override
	public void setItem(Map<String, Actress> item) {
		this.item = item;
	}

	@Override
	public Map<String, Actress> getItem() {
		return item;
	}

}
