package jk.kamoru.crazy.shop.finder;

import java.util.List;

import jk.kamoru.crazy.domain.History;
import jk.kamoru.crazy.domain.Search;
import jk.kamoru.crazy.shop.warehouse.Warehouse;
import jk.kamoru.util.StringUtils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class HistoryFinder {

	@Autowired Warehouse warehouse;

	public List<History> find(Search search) {
		List<History> found = warehouse.getHistories();
		for (History history : found)
			if (!StringUtils.equalsIgnoreCase(history.getOpus(), search.getSearchText()) 
					&& !StringUtils.containsIgnoreCase(history.getDesc(), search.getSearchText()))
				found.remove(history);
		return found;
	}

}
