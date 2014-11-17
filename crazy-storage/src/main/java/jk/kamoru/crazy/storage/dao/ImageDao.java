package jk.kamoru.crazy.storage.dao;

import java.util.List;

import jk.kamoru.crazy.domain.Image;

public interface ImageDao {

	Image getImage(Integer idx);

	int getImageSourceSize();

	List<Image> getImageList();

	void delete(Integer idx);

}
