package jk.kamoru.crazy.service;

import java.util.Collection;

/**
 * 정제기
 * <p>수확기({@link jk.kamoru.crazy.service.Harvester Harvester})에서 
 * 받은 수확물을 정제하여 상품으로 만들어 창고({@link jk.kamoru.crazy.service.Warehouse Warehouse})에 저장한다.</p>
 * @author kamoru
 *
 */
public interface Purifier<E> {
	
	/**
	 * 수확기({@link jk.kamoru.crazy.service.Harvester Harvester})에서 수확물을 전달 받는다
	 * @param collection
	 */
	void fromHarvester(Collection<E> items);

	/**
	 * 수확물 정제 후 창고({@link jk.kamoru.crazy.service.Warehouse Warehouse})에 저장한다
	 */
	void purifyAndToWarehouse();
	
}
