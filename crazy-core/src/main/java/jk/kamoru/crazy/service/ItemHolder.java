package jk.kamoru.crazy.service;

public interface ItemHolder<T> {

	void setItem(T item);
	
	T getItem();
}
