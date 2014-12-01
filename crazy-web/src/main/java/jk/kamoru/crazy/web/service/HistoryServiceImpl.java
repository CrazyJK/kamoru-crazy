package jk.kamoru.crazy.web.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jk.kamoru.crazy.CrazyException;
import jk.kamoru.crazy.domain.Action;
import jk.kamoru.crazy.domain.Actress;
import jk.kamoru.crazy.domain.History;
import jk.kamoru.crazy.domain.Search;
import jk.kamoru.crazy.domain.Studio;
import jk.kamoru.crazy.domain.Video;
import jk.kamoru.crazy.service.CrazyShop;
import jk.kamoru.util.StringUtils;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class HistoryServiceImpl implements HistoryService {

	@Autowired CrazyShop crazyShop;
	
	@Override
	public void persist(History history) {
//		try {
//			crazyShop.persist(history);
//		} catch (IOException e) {
//			throw new CrazyException("history persist error", e);
//		}
	}

	@Override
	public List<History> findByOpus(String opus) {
		log.info("find opus {}", opus);
		if (StringUtils.isBlank(opus))
			return dummyList();
		return crazyShop.findHistory(new Search(opus));
	}

	@Override
	public List<History> findByVideo(Video video) {
//		return crazyShop.findByVideo(video);
		return crazyShop.findHistory(new Search(video.getOpus()));
	}

	@Override
	public List<History> findByStudio(Studio studio) {
//		return crazyShop.findByVideo(studio.getVideoList());
		return crazyShop.findHistory(new Search(studio.getName()));
	}

	@Override
	public List<History> findByActress(Actress actress) {
//		return crazyShop.findByVideo(actress.getVideoList());
		return crazyShop.findHistory(new Search(actress.getName()));
	}

	@Override
	public List<History> findByQuery(String query) {
		if (StringUtils.isBlank(query))
			return dummyList();
//		return crazyShop.find(query);
		return crazyShop.findHistory(new Search(query));
	}

	@Override
	public List<History> getAll() {
//		return crazyShop.getList();
		return crazyShop.findHistory(new Search(""));
	}

	@Override
	public List<History> findByDate(Date date) {
//		return crazyShop.findByDate(date);
		return crazyShop.findHistory(new Search(date.toString()));
	}

	@Override
	public List<History> findByAction(Action action) {
//		return crazyShop.findByAction(action);
		return crazyShop.findHistory(new Search(action.toString()));
	}
	
	@Override
	public boolean contains(String opus) {
//		return crazyShop.findByOpus(opus).size() > 0;
		return crazyShop.findHistory(new Search(opus)).size() > 0;
	}
	
	private List<History> dummyList() {
		return new ArrayList<History>();
	}

	@Override
	public List<History> getDeduplicatedList() {
		Map<String, History> found = new HashMap<String, History>();
		for (History history : crazyShop.findHistory(new Search(""))) {
			if (!found.containsKey(history.getOpus()))
				found.put(history.getOpus(), history);
		}
		return new ArrayList<History>(found.values());
	}
	
}
