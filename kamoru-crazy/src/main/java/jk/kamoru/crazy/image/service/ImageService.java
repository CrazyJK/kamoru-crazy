package jk.kamoru.crazy.image.service;

import java.util.List;

import jk.kamoru.crazy.image.domain.Image;

public interface ImageService {

	Image getImage(int idx);

	int getImageSourceSize();

	void reload();

	Image getImageByRandom();

	void downloadGnomImage();

	List<Image> getImageList();

	String getImageNameJSON();

	void delete(int idx);

	int getRandomImageNo();
}
