package jk.kamoru.crazy.shop.harvester;


/**
 * 수확기
 * <p>작물을 정기적으로 수집
 * 수확물을 정제기({@link jk.kamoru.crazy.shop.purifier.Purifier Purifier})에 전달한다</p>
 * @author kamoru
 *
 */
public interface Harvester {

	void videoHarvest();
	
	void imageHarvest();
	
	void historyHarvest();
	
}
