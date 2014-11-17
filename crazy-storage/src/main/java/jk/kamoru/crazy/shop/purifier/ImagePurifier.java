package jk.kamoru.crazy.shop.purifier;

import java.io.File;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import jk.kamoru.crazy.domain.Image;
import jk.kamoru.crazy.service.Purifier;
import jk.kamoru.crazy.service.Warehouse;
import jk.kamoru.crazy.shop.holder.ImageHolder;
import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;

@Slf4j
public class ImagePurifier implements Purifier<File> {

	private Collection<File> items;
	
	private List<Image> imageList;

	@Autowired Warehouse warehouse;
	
	@Override
	public void fromHarvester(Collection<File> items) {
		this.items = items;
		
		purifyAndToWarehouse();
	}

	@Override
	public void purifyAndToWarehouse() {
		int idx = 0;
		for (File file : items) {
			imageList.add(new Image(file, idx++));
		}
		
		Collections.sort(imageList, new Comparator<Image>() {
				@Override
				public int compare(Image o1, Image o2) {
					return NumberUtils.compare(o1.getLastModified(), o2.getLastModified());
				}
			});
		log.info("Total found image {}", imageList.size());
		
		toWarehouse();
	}

	public void toWarehouse() {
		warehouse.fromPurifier(new ImageHolder(imageList));
	}

}
