package jk.kamoru.crazy.shop.harvester;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.annotation.PostConstruct;

import jk.kamoru.crazy.CRAZY;
import jk.kamoru.crazy.CrazyException;
import jk.kamoru.crazy.service.Harvester;
import jk.kamoru.crazy.shop.purifier.HistoryPurifier;
import jk.kamoru.crazy.shop.purifier.ImagePurifier;
import jk.kamoru.crazy.shop.purifier.VideoPurifier;
import jk.kamoru.util.FileUtils;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;

@Slf4j
public class FileHarvester {

	@Autowired VideoPurifier videoPurifier;
	@Autowired ImagePurifier imagePurifier;
	@Autowired HistoryPurifier historyPurifier;

	private Collection<File> videoFiles;
	private Collection<File> imageFiles;
	private Collection<File> historyFiles;

	private List<String> videoStoragePaths;
	private List<String> imageStoragePaths;

	@PostConstruct
	private void afterPropertySet() {
		if (videoStoragePaths == null)
			throw new CrazyException("videoStoragePaths must be set");
		if (imageStoragePaths == null)
			throw new CrazyException("imageStoragePaths must be set");
		
		videoFiles = new ArrayList<File>();
		imageFiles = new ArrayList<File>();
		historyFiles = new ArrayList<File>();
	}
	
	public void harvest() {
		synchronized (CRAZY.class) {
			
			videoHarvest();
			imageHarvest();
			historyHarvest();

			toPurifier();
		}
	}
	
	@SuppressWarnings("unused")
	private List<File> gather(List<String> paths, String...filter) {
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
	
	private void videoHarvest() {
		for (String path : videoStoragePaths) {
			File directory = new File(path);
			log.debug("Harvesting video : {}", directory.getAbsolutePath());
			if (directory.isDirectory()) {
				Collection<File> found = FileUtils.listFiles(directory, null, true);
				log.debug("\tfound {}", found.size());
				videoFiles.addAll(found);
			}
			else {
				log.warn("\tIt is not directory. Pass!!!");
			}
		}
		log.info("Total found ", videoFiles.size());
	}
	
	private void imageHarvest() {
		for (String path : this.imageStoragePaths) {
			File directory = new File(path);
			log.debug("Harvesting image : {}", directory.getAbsolutePath());
			if (directory.isDirectory()) {
				Collection<File> found = FileUtils.listFiles(directory, new String[] {"jpg", "jpeg", "gif", "png" }, true);
				log.debug("\tfound {}", found.size());
				imageFiles.addAll(found);
			}
			else {
				log.warn("\tIt is not directory. Pass!!!");
			}
		}
		log.info("Total found {}", imageFiles.size());
	}

	private void historyHarvest() {
		File historyFile = new File(videoStoragePaths[0], "history.log");
		historyFiles.add(historyFile);
		log.debug("history file is {}", historyFile.getAbsolutePath());
	}

	@Override
	public void toPurifier() {
		videoPurifier.fromHarvester(videoFiles);
		imagePurifier.fromHarvester(imageFiles);
		historyPurifier.fromHarvester(historyFiles);
	}

}
