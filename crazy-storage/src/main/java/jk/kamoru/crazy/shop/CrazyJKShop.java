package jk.kamoru.crazy.shop;

import java.util.List;

import jk.kamoru.crazy.domain.Actress;
import jk.kamoru.crazy.domain.History;
import jk.kamoru.crazy.domain.Image;
import jk.kamoru.crazy.domain.Search;
import jk.kamoru.crazy.domain.Studio;
import jk.kamoru.crazy.domain.Video;
import jk.kamoru.crazy.service.CrazyShop;
import jk.kamoru.crazy.shop.finder.HistoryFinder;
import jk.kamoru.crazy.shop.finder.ImageFinder;
import jk.kamoru.crazy.shop.finder.VideoFinder;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CrazyJKShop implements CrazyShop {

	@Autowired VideoFinder videoFinder;
	@Autowired ImageFinder imageFinder;
	@Autowired HistoryFinder historyFinder;

	@Override
	public Video getVideo() {
		return videoFinder.getVideo();
	}
	@Override
	public Video getVideo(String opus) {
		return videoFinder.getVideo(opus);
	}
	@Override
	public Studio getStudio(String name) {
		return videoFinder.getStudio(name);
	}
	@Override
	public Actress getActress(String name) {
		return videoFinder.getActress(name);
	}
	@Override
	public List<Video> findVideo(Search search) {
		return videoFinder.findVideo(search);
	}
	@Override
	public List<Studio> findStudio(Search search) {
		return videoFinder.findStudio(search);
	}
	@Override
	public List<Actress> findActress(Search search) {
		return videoFinder.findActress(search);
	}
	/* set Rank
	 * set Overview
	 * request play
	 * @see jk.kamoru.crazy.service.CrazyShop#feedbackVideo(jk.kamoru.crazy.domain.Video)
	 */
	@Override
	public void feedbackVideo(Video video) {
		// TODO Auto-generated method stub
		
	}
	/* set info
	 * @see jk.kamoru.crazy.service.CrazyShop#feedbackStudio(jk.kamoru.crazy.domain.Studio)
	 */
	@Override
	public void feedbackStudio(Studio studio) {
		// TODO Auto-generated method stub
		
	}
	/* set info
	 * @see jk.kamoru.crazy.service.CrazyShop#feedbackActress(jk.kamoru.crazy.domain.Actress)
	 */
	@Override
	public void feedbackActress(Actress actress) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public Image getImage() {
		return imageFinder.getImage();
	}
	@Override
	public Image getImage(Integer idx) {
		return imageFinder.getImage(idx);
	}
	@Override
	public List<Image> findImage(Search search) {
		return imageFinder.find(search);
	}
	@Override
	public void feedbackImage(Integer idx) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public List<History> findHistory(Search search) {
		return historyFinder.find(search);
	}
	

}