package jk.kamoru.crazy.service;

import java.util.Map;

/**
 * 창고
 * <p>정제기({@link jk.kamoru.crazy.service.Purifier Purifier})가 
 * 만든 상품을 담고 있다가 원하는 곳에 출고한다.<p>
 * @author kamoru
 * @param <K>
 *
 */
public interface Warehouse {

	<T> void fromPurifier(ItemHolder<T> holder);
	
	/**
	 * 상품을 출고한다
	 * @return
	 */
//	V deliver(K k);
	
}
