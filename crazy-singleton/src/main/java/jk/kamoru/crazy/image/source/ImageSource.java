package jk.kamoru.crazy.image.source;

import java.util.List;

import jk.kamoru.crazy.image.domain.Image;

public interface ImageSource {

	Image getImage(int idx);
	
	List<Image> getImageList();

	int getImageSourceSize();
	
	void reload();

	void delete(int idx);
	
}
