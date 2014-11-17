package jk.kamoru.crazy.shop.warehouse;

import java.util.List;

import jk.kamoru.crazy.service.ItemHolder;
import jk.kamoru.crazy.service.Warehouse;

public class CrazyWarehouse implements Warehouse {

	List<ItemHolder> list;
	
	@Override
	public <T> void fromPurifier(ItemHolder<T> holder) {
		// TODO Auto-generated method stub

	}

}
