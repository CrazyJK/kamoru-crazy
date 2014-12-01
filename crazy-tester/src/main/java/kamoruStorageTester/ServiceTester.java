package kamoruStorageTester;

import java.util.List;

import jk.kamoru.crazy.domain.Search;
import jk.kamoru.crazy.domain.Video;
import jk.kamoru.crazy.service.CrazyShop;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


//@EnableScheduling
@Component
public class ServiceTester {

	private CrazyShop storageService;
	
	@Autowired
	public void setVideoService(CrazyShop videoService) {
		this.storageService = videoService;
	}
	
//	@Scheduled(fixedRate = 5000)
	public void getVideo() {
		Video video = storageService.getVideo();
		System.out.println(video.getOpus() + " " + video.getTitle());
	}
	
	public void getVideoList() {
		Search search = new Search();
		search.setSearchText("BBI");
		List<Video> list = storageService.findVideo(search);
		for (Video video : list)
			System.out.println(video.getFullname());
	}
}
