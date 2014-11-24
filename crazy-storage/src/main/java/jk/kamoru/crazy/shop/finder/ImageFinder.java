package jk.kamoru.crazy.shop.finder;

import java.util.List;

import jk.kamoru.crazy.ImageNotFoundException;
import jk.kamoru.crazy.domain.Image;
import jk.kamoru.crazy.domain.Search;
import jk.kamoru.crazy.shop.warehouse.Warehouse;
import jk.kamoru.util.StringUtils;

import org.apache.commons.lang.math.RandomUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ImageFinder {

	@Autowired Warehouse warehouse;
	
	public Image getImage() {
		List<Image> list = warehouse.getImages();
		return list.get(RandomUtils.nextInt(list.size()));
	}

	public Image getImage(Integer idx) {
		List<Image> list = warehouse.getImages();
		if (list.size() >= idx) 
			throw new ImageNotFoundException(idx);
		return list.get(idx);
	}

	public List<Image> find(Search search) {
		List<Image> found = warehouse.getImages();
		for (Image image : found)
			if (!StringUtils.containsAny(image.getName(), search.getSearchText())) 
				found.remove(image);
		return found;
	}

}
