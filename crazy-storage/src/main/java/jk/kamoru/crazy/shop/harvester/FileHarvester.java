package jk.kamoru.crazy.shop.harvester;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.annotation.PostConstruct;

import jk.kamoru.crazy.CRAZY;
import jk.kamoru.crazy.CrazyException;
import jk.kamoru.crazy.shop.purifier.HistoryPurifier;
import jk.kamoru.crazy.shop.purifier.ImagePurifier;
import jk.kamoru.crazy.shop.purifier.VideoPurifier;
import jk.kamoru.crazy.shop.warehouse.Warehouse;
import jk.kamoru.util.FileUtils;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;

@Slf4j
public class FileHarvester implements Harvester {
	
	@Autowired Warehouse warehouse;
	
	private VideoPurifier videoPurifier;
	private ImagePurifier imagePurifier;
	private HistoryPurifier historyPurifier;

	private List<String> videoStoragePaths;
	private List<String> imageStoragePaths;

	@PostConstruct
	private void afterPropertySet() {
		if (videoStoragePaths == null)
			throw new CrazyException("videoStoragePaths must be set");
		if (imageStoragePaths == null)
			throw new CrazyException("imageStoragePaths must be set");
		videoPurifier = new VideoPurifier();
		imagePurifier = new ImagePurifier();
		historyPurifier = new HistoryPurifier();
	}
	
	private List<File> gathering(List<String> paths, String[] filter) {
		List<File> files = new ArrayList<File>();
		for (String path : paths) {
			File directory = new File(path);
			log.info("Scanning {}", directory.getAbsolutePath());
			if (directory.isDirectory()) {
				Collection<File> found = FileUtils.listFiles(directory, filter, true);
				log.debug("\tfound {}", found.size());
				files.addAll(found);
			}
			else {
				log.warn("\tIt is not directory. Pass!!!");
			}
		}
		log.info("Total found ", files.size());
		return files;
	}
	
	
	@Override
	public void videoHarvest() {
		videoPurifier.setFiles(gathering(videoStoragePaths, null));
		warehouse.pushVideoPurifier(videoPurifier);
	}
	
	@Override
	public void imageHarvest() {
		imagePurifier.setFiles(gathering(imageStoragePaths, CRAZY.IMAGE_FILTER.split(",")));
		warehouse.pushImagePurifier(imagePurifier);
	}

	@Override
	public void historyHarvest() {
		historyPurifier.setFile(new File(videoStoragePaths.get(0), CRAZY.HISTORY_LOG));
		warehouse.pushHistoryPurifier(historyPurifier);
	}

}
