package jk.kamoru.crazy.shop.holder;

import java.util.List;

import jk.kamoru.crazy.domain.History;
import jk.kamoru.crazy.service.ItemHolder;

public class HistoryHolder implements ItemHolder<List<History>> {

	List<History> item;

	public HistoryHolder(List<History> item) {
		super();
		this.item = item;
	}

	@Override
	public void setItem(List<History> item) {
		this.item = item;
	}

	@Override
	public List<History> getItem() {
		return item;
	}
	

}
