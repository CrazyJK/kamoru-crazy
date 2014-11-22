package jk.kamoru.crazy.shop.warehouse;

import java.util.List;
import java.util.Map;

import jk.kamoru.crazy.service.ItemHolder;
import jk.kamoru.crazy.service.ItemType;
import jk.kamoru.crazy.service.Warehouse;

public class CrazyWarehouse implements Warehouse {

	Map<ItemType, Map<String, T>> items;
	
	@Override
	public <T> void fromPurifier(Map<ItemType, Map<String, T>> items) {
		// TODO Auto-generated method stub
		
	}


}
