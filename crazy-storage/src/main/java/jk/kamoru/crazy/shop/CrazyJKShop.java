package jk.kamoru.crazy.shop;

import java.util.List;
import java.util.Random;

import jk.kamoru.crazy.ActressNotFoundException;
import jk.kamoru.crazy.ImageNotFoundException;
import jk.kamoru.crazy.StudioNotFoundException;
import jk.kamoru.crazy.VideoNotFoundException;
import jk.kamoru.crazy.domain.Actress;
import jk.kamoru.crazy.domain.History;
import jk.kamoru.crazy.domain.Image;
import jk.kamoru.crazy.domain.Search;
import jk.kamoru.crazy.domain.Studio;
import jk.kamoru.crazy.domain.Video;
import jk.kamoru.crazy.service.CrazyShop;
import jk.kamoru.crazy.shop.dao.ActressDao;
import jk.kamoru.crazy.shop.dao.HistoryDao;
import jk.kamoru.crazy.shop.dao.ImageDao;
import jk.kamoru.crazy.shop.dao.StudioDao;
import jk.kamoru.crazy.shop.dao.VideoDao;
import jk.kamoru.util.StringUtils;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;

@Slf4j
public class CrazyJKShop implements CrazyShop {

	@Autowired VideoDao videoDao;
	@Autowired StudioDao studioDao;
	@Autowired ActressDao actressDao;
	@Autowired ImageDao imageDao;
	@Autowired HistoryDao historyDao;
	
	@Override
	public Video getVideo(String opus) throws VideoNotFoundException {
		return videoDao.get(opus);
	}

	@Override
	public Studio getStudio(String name) throws StudioNotFoundException {
		return studioDao.get(name);
	}

	@Override
	public Actress getActress(String name) throws ActressNotFoundException {
		return actressDao.get(name);
	}

	@Override
	// XXX trace log
	public List<Video> findVideo(Search search) {
		return videoDao.find(search);
	}

	@Override
	public List<Studio> findStudio(Search search) {
		List<Studio> found = studioDao.list();
		if (StringUtils.isNotBlank(search.getSearchText()))
			for (Studio studio : found)
				if (!StringUtils.equalsIgnoreCase(studio.getName(), search.getSearchText()))
					found.remove(studio);
		if (search.getSelectedStudio().size() > 0)
			for (Studio studio : found)
				if (!search.getSelectedStudio().contains(studio.getName()))
					found.remove(studio);
		return found;
	}

	@Override
	public List<Actress> findActress(Search search) {
		List<Actress> found = actressDao.list();
		if (StringUtils.isNotBlank(search.getSearchText()))
			for (Actress actress : found)
				if (!StringUtils.equalsIgnoreCase(actress.getName(), search.getSearchText()))
					found.remove(actress);
		if (search.getSelectedActress().size() > 0)
			for (Actress actress : found)
				if (!search.getSelectedActress().contains(actress.getName()))
					found.remove(actress);
		return found;
	}

	@Override
	public void mergeVideo(Video video) {
		Video originalVideo = videoDao.get(video.getOpus());
		originalVideo.merge(video);
	}

	@Override
	public void mergeStudio(Studio studio) {
		Studio originalStudio = studioDao.get(studio.getName());
		originalStudio.merge(studio);
	}

	@Override
	public void mergeActress(Actress actress) {
		Actress originalActress = actressDao.get(actress.getName());
		originalActress.merge(actress);
	}
	
	@Override
	public Image getImage(Integer idx) throws ImageNotFoundException {
		return imageDao.get(idx);
	}

	@Override
	public Image getImageByRandom() throws ImageNotFoundException {
		int total = imageDao.size();
		int idx = new Random().nextInt(total);
		return imageDao.get(idx);
	}

	@Override
	public List<Image> getImageList() {
		return imageDao.list();
	}

	@Override
	public Integer getImageSize() {
		return imageDao.size();
	}

	@Override
	public void removeImage(Integer idx) {
		imageDao.remove(idx);
	}

	@Override
	public List<History> findHistory(Search search) {
		List<History> found = historyDao.list();
		for (History history : found)
			if (!StringUtils.equalsIgnoreCase(history.getOpus(), search.getSearchText()) 
					&& !StringUtils.containsIgnoreCase(history.getDesc(), search.getSearchText()))
				found.remove(history);
		return found;
	}

	@Override
	public List<History> getHistoryList() {
		return historyDao.list();
	}

}