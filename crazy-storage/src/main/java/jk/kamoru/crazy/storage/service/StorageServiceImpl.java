package jk.kamoru.crazy.storage.service;

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
import jk.kamoru.crazy.service.StorageService;
import jk.kamoru.crazy.storage.dao.HistoryDao;
import jk.kamoru.crazy.storage.dao.ImageDao;
import jk.kamoru.crazy.storage.dao.VideoDao;
import jk.kamoru.util.StringUtils;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;

@Slf4j
public class StorageServiceImpl implements StorageService {

	@Autowired VideoDao videoDao;
	@Autowired ImageDao imageDao;
	@Autowired HistoryDao historyDao;
	
	@Override
	public Video getVideo(String opus) throws VideoNotFoundException {
		return videoDao.getVideo(opus);
	}

	@Override
	public Studio getStudio(String name) throws StudioNotFoundException {
		return videoDao.getStudio(name);
	}

	@Override
	public Actress getActress(String name) throws ActressNotFoundException {
		return videoDao.getActress(name);
	}

	@Override
	// XXX trace log
	public List<Video> findVideo(Search search) {
		List<Video> found = videoDao.getVideoList();
		log.trace("total {}", found.size());
		// 검색어
		if (StringUtils.isNotBlank(search.getSearchText()))
			for (Video video : found)
				if (!video.containsQuery(search.getSearchText()))
					found.remove(video);
		log.trace("  by search text {}", found.size());
		// 추가 검색 조건 : 파일, 자막 존재여부
		if (search.isAddCond())
			for (Video video : found)
				if (!video.checkExistCondition(search.isExistVideo(), search.isExistSubtitles()))
					found.remove(video);
		log.trace("  by add cond {}", found.size());
		// rank
		if (search.getRankRange().size() > 0)
			for (Video video : found)
				if (!search.getRankRange().contains(video.getRank()))
					found.remove(video);
		log.trace("  by rank {}", found.size());
		// play count
		if (search.getPlayCount() > -1)
			for (Video video : found)
				if (video.getPlayCount() != search.getPlayCount())
					found.remove(video);
		log.trace("  by play count {}", found.size());
		// studio
		if (search.getSelectedStudio().size() > 0)
			for (Video video : found)
				if (!search.getSelectedStudio().contains(video.getStudio().getName()))
					found.remove(video);
		log.trace("  by studio {}", found.size());
		// actress
		if (search.getSelectedActress().size() > 0)
			for (Video video : found)
				if (!video.containsAnyActressList(search.getSelectedActress()))
					found.remove(video);
		log.trace("  by actress {}", found.size());
		return found;
	}

	@Override
	public List<Studio> findStudio(Search search) {
		List<Studio> found = videoDao.getStudioList();
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
		List<Actress> found = videoDao.getActressList();
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
		Video originalVideo = videoDao.getVideo(video.getOpus());
		originalVideo.merge(video);
	}

	@Override
	public void mergeStudio(Studio studio) {
		Studio originalStudio = videoDao.getStudio(studio.getName());
		originalStudio.merge(studio);
	}

	@Override
	public void mergeActress(Actress actress) {
		Actress originalActress = videoDao.getActress(actress.getName());
		originalActress.merge(actress);
	}
	
	@Override
	public Image getImage(Integer idx) throws ImageNotFoundException {
		return imageDao.getImage(idx);
	}

	@Override
	public Image getImageByRandom() throws ImageNotFoundException {
		int total = imageDao.getImageSourceSize();
		int idx = new Random().nextInt(total);
		return imageDao.getImage(idx);
	}

	@Override
	public List<Image> getImageList() {
		return imageDao.getImageList();
	}

	@Override
	public Integer getImageSize() {
		return imageDao.getImageSourceSize();
	}

	@Override
	public void removeImage(Integer idx) {
		imageDao.delete(idx);
	}

	@Override
	public List<History> findHistory(Search search) {
		List<History> found = historyDao.getHistoryList();
		for (History history : found)
			if (!StringUtils.equalsIgnoreCase(history.getOpus(), search.getSearchText()) 
					&& !StringUtils.containsIgnoreCase(history.getDesc(), search.getSearchText()))
				found.remove(history);
		return found;
	}

	@Override
	public List<History> getHistoryList() {
		return historyDao.getHistoryList();
	}

}