package jk.kamoru.crazy.service;

import java.io.File;
import java.util.Collection;
import java.util.Map;

/**
 * 정제기
 * <p>수확기({@link jk.kamoru.crazy.service.Harvester Harvester})에서 받은 수확물을 정제하여 
 * 상품으로 만들어 전달한다.</p>
 * @param <T> 정제할 아이템 타입
 * 
 * @author kamoru
 *
 */
public interface Purifier<T> {
	
	/**
	 * 수확기({@link jk.kamoru.crazy.service.Harvester Harvester})에서 수확물을 전달 받아
	 * 정제하여 되돌려 준다
	 */
	Map<String, T> purify(Collection<File> items);
	
}
