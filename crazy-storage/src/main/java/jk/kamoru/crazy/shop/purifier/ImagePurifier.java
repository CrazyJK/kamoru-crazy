package jk.kamoru.crazy.shop.purifier;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import jk.kamoru.crazy.domain.Image;
import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang.math.NumberUtils;

@Slf4j
public class ImagePurifier {

	private List<File> imageFiles;

	public void setFiles(List<File> files) {
		this.imageFiles = files;
	}
	
	private List<Image> purify() {
		int idx = 0;
		List<Image> imageList = new ArrayList<Image>();
		for (File file : imageFiles) {
			imageList.add(new Image(file, idx++));
		}
		Collections.sort(imageList, new Comparator<Image>() {
				@Override
				public int compare(Image o1, Image o2) {
					return NumberUtils.compare(o1.getLastModified(), o2.getLastModified());
				}
			});
		log.info("Total found image {}", imageList.size());
		return imageList;
	}

	public List<Image> list() {
		return purify();
	}
}
