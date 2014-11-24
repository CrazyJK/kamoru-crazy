package jk.kamoru.crazy.shop.finder;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import jk.kamoru.crazy.ActressNotFoundException;
import jk.kamoru.crazy.StudioNotFoundException;
import jk.kamoru.crazy.VideoNotFoundException;
import jk.kamoru.crazy.domain.Actress;
import jk.kamoru.crazy.domain.Search;
import jk.kamoru.crazy.domain.Studio;
import jk.kamoru.crazy.domain.Video;
import jk.kamoru.crazy.shop.warehouse.Warehouse;
import jk.kamoru.util.StringUtils;
import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang.math.RandomUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class VideoFinder {
	
	@Autowired Warehouse warehouse;

	public Video getVideo() {
		List<Video> list = listVideo();
		return list.get(RandomUtils.nextInt(list.size()));
	}

	public Video getVideo(String opus) {
		Map<String, Video> videos = warehouse.getVideos();
		if (!videos.containsKey(opus))
			throw new VideoNotFoundException(opus);
		return videos.get(opus);
	}

	public Studio getStudio(String name) {
		Map<String, Studio> studios = warehouse.getStudios();
		if (!studios.containsKey(name))
			throw new StudioNotFoundException(name);
		return studios.get(name);
	}

	public Actress getActress(String name) {
		Map<String, Actress> actresses = warehouse.getActresses();
		if (!actresses.containsKey(name))
			throw new ActressNotFoundException(name);
		return actresses.get(name);
	}
	
	public List<Video> findVideo(Search search) {
		List<Video> found = listVideo();
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

	public List<Studio> findStudio(Search search) {
		List<Studio> found = listStudio();
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

	public List<Actress> findActress(Search search) {
		List<Actress> found = listActress();
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

	private List<Video> listVideo() {
		return new ArrayList<Video>(warehouse.getVideos().values());
	}
	
	private List<Studio> listStudio() {
		return new ArrayList<Studio>(warehouse.getStudios().values());
	}
	
	private List<Actress> listActress() {
		return new ArrayList<Actress>(warehouse.getActresses().values());
	}
}

