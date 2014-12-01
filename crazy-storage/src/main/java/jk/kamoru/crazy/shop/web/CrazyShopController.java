package jk.kamoru.crazy.shop.web;

import java.util.List;

import jk.kamoru.crazy.domain.Search;
import jk.kamoru.crazy.domain.Video;
import jk.kamoru.crazy.service.CrazyShop;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CrazyShopController {

	@Autowired CrazyShop crazyShop;

	@RequestMapping("/video")
	public Video video() {
		return crazyShop.getVideo();
	}
	
	@RequestMapping("/video/list")
	public List<Video> list() {
		Search search = new Search();
		search.setSearchText("BBI");
		return crazyShop.findVideo(search); 
	}
}
