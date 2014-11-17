package jk.kamoru.crazy.shop.source;

import java.util.List;

public interface StorageHolder<T, K> {

	T get(K key);
	
	List<T> list();
	
	
}
