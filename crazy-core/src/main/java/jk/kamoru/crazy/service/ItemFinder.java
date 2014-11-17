package jk.kamoru.crazy.service;

import java.util.List;

/**
 * 상품 검색
 * <p>창고({@link jk.kamoru.crazy.storage.source.Warehouse Warehouse})에서 
 * 소비가자 원하는 상품을 찾아 온다<br>
 * {@link jk.kamoru.crazy.service.CrazyShop CrazyShop}에서 일한다</p>
 * @author kamoru
 *
 * @param <T> 상품
 * @param <K> 상품명
 * @param <S> 검색어
 */
public interface ItemFinder<T, K, S> {

	/**
	 * 단품 찾기
	 * @param key
	 * @return
	 */
	T get(K key);
	
	/**
	 * 상품 찾기
	 * @param search
	 * @return
	 */
	List<T> find(S search);
	
	/**
	 * 현재 재고 수량
	 * @return
	 */
	Integer size();

}
