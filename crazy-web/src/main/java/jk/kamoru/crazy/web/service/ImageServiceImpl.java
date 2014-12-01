package jk.kamoru.crazy.web.service;

import java.util.List;

import jk.kamoru.crazy.domain.Image;
import jk.kamoru.crazy.domain.Search;
import jk.kamoru.crazy.service.CrazyShop;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ImageServiceImpl implements ImageService {

	@Autowired
	private CrazyShop crazyShop;
	
	@Override
	public Image getImage(int idx) {
		return crazyShop.getImage(idx);
	}

	@Override
	public int getImageSourceSize() {
		return crazyShop.findImage(new Search("")).size();
	}

	@Override
	public void reload() {
//		crazyShop.reload();
	}

	@Override
	public Image getImageByRandom() {
		return crazyShop.getImage(getRandomImageNo());
	}

	@Override
	public List<Image> getImageList() {
		return crazyShop.findImage(new Search(""));
	}

	@Override
	public String getImageNameJSON() {
		StringBuilder sb = new StringBuilder("{");
		int index = 0;
		for (Image image : crazyShop.findImage(new Search(""))) {
			if (index > 0)
				sb.append(",");
			sb.append(String.format("\"%s\":\"%s\"", index++, image.getName()));
		}
		sb.append("}");
		return sb.toString();
	}

	@Override
	public void delete(int idx) {
//		crazyShop.delete(idx);
	}

	@Override
	public int getRandomImageNo() {
		return (int)(Math.random() * crazyShop.findImage(new Search("")).size());
	}
}
