package jk.kamoru.crazy.shop.source.ref;

import java.util.List;

import jk.kamoru.crazy.domain.Image;

public interface ImageSource {

	Image getImage(int idx);
	
	List<Image> getImageList();

	int getImageSourceSize();
	
	void reload();

	void remove(int idx);
	
}
