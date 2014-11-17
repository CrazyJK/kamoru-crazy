package jk.kamoru.crazy.shop.finder;

import java.util.List;

import jk.kamoru.crazy.domain.Search;
import jk.kamoru.crazy.domain.Video;
import jk.kamoru.crazy.service.ItemFinder;
import jk.kamoru.crazy.shop.dao.VideoDao;
import jk.kamoru.crazy.shop.source.ref.VideoSource;
import jk.kamoru.util.StringUtils;
import lombok.extern.slf4j.Slf4j;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
@Slf4j
public class VideoFinder implements ItemFinder<Video, String, Search> {
	
	protected static final Logger logger = LoggerFactory.getLogger(VideoFinder.class);

	@Autowired
	private VideoSource videoSource;

	public List<Video> find(Search search) {
		List<Video> found = videoSource.getVideoList();
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
	public Video get(Object opus) {
		videoSource.getVideo(opus.toString());
		return null;
	}

	@Override
	public List<Video> list() {
		return videoSource.getVideoList();
	}

	@Override
	public Integer size() {
		return videoSource.getVideoList().size();
	}

	@Override
	public void remove(String opus) {
		videoSource.removeVideo(opus);
	}
	
}

