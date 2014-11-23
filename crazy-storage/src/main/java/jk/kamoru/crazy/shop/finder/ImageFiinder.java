package jk.kamoru.crazy.shop.finder;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import jk.kamoru.crazy.domain.Image;
import jk.kamoru.crazy.shop.dao.ImageDao;
import jk.kamoru.crazy.shop.source.ref.ImageSource;
import jk.kamoru.util.StringUtils;

@Repository
public class ImageFiinder implements ItemFinder<Image, Integer, String> {

	@Autowired ImageSource imageSource;
	
	@Override
	public Image get(Object idx) {
		return imageSource.getImage(Integer.valueOf(idx.toString()));
	}

	@Override
	public Integer size() {
		return imageSource.getImageSourceSize();
	}

	@Override
	public List<Image> list() {
		return imageSource.getImageList();
	}

	@Override
	public void remove(Integer idx) {
		imageSource.remove(idx);
	}

	@Override
	public List<Image> find(String search) {
		List<Image> found = imageSource.getImageList();
		for (Image image : found)
			if (!StringUtils.containsAny(image.getName(), search)) 
				found.remove(image);
		return found;
	}

}
