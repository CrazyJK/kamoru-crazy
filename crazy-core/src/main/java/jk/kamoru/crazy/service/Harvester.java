package jk.kamoru.crazy.service;

/**
 * 수확기
 * <p>작물을 정기적으로 수집
 * 수확물을 정제기({@link jk.kamoru.crazy.service.Purifier Purifier})에 전달한다</p>
 * @author kamoru
 *
 */
public interface Harvester {

	/**
	 * 수확물 수집
	 */
	void harvest();
	
	/**
	 * 수집된 내용을 정제기({@link jk.kamoru.crazy.service.Purifier Purifier})에 전달
	 */
	void toPurifier();
	
}
