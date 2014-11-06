package jk.kamoru.crazy.video.source.storage;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import jk.kamoru.core.storage.Storage;
import jk.kamoru.core.storage.StorageException;
import jk.kamoru.crazy.video.domain.Video;
import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang3.StringUtils;

@Slf4j
public class VideoStorage implements Storage<Video> {

	private transient List<Video> elements;
	
	private long capacity;
	
	private long space;
	
	private String name;

	private VideoStorage(String name) {
		this(name, -1l);
	}
	
	private VideoStorage(String name, long capacity) {
		elements = new ArrayList<Video>();
		initialize(name, capacity);
	}

	public static Storage<Video> newStorage(String name) {
		return new VideoStorage(name);
	}
	
	public static Storage<Video> newStorage(String name, long capacity) {
		return new VideoStorage(name, capacity);
	}

	@Override
	public void initialize(String name) {
		initialize(name, -1l);
	}

	@Override
	public void initialize(String name, long capacity) {
		this.name = name;
		this.capacity = capacity;
	}

	@Override
	public void add(Video e) {
		long length = e.getLength();
		if (checkCapacity(length)) {
			elements.add(e);
			space += length;
		}
		log.debug("add {} current space {}/{}", e.getOpus(), space, capacity);
	}

	@Override
	public void addAll(Collection<Video> c) {
		long length = 0;
		for (Video video : c)
			length += video.getLength();
		if (checkCapacity(length)) {
			elements.addAll(c);
			space += length;
		}
		log.debug("addAll {}", c.size());
	}

	@Override
	public void remove(int index) throws StorageException {
		remove(get(index));
	}

	@Override
	public void remove(String key) throws StorageException {
		remove(get(key));
	}

	@Override
	public void remove(Video e) throws StorageException {
		elements.remove(e);
		space -= e.getLength();
		log.debug("remove {} current space {}/{}", e.getOpus(), space, capacity);
	}

	@Override
	public void removeAll() throws StorageException {
		for (Video video : elements)
			remove(video);
		log.debug("remove all current space {}/{}", space, capacity);
	}

	@Override
	public void delete(int index) throws StorageException {
		delete(get(index));
	}

	@Override
	public void delete(String key) throws StorageException {
		delete(get(key));
	}

	@Override
	public void delete(Video e) {
		elements.remove(e);
		space -= e.getLength();
		e.removeVideo();
		log.debug("delete {} current space {}/{}", e.getOpus(), space, capacity);
	}

	@Override
	public void deleteAll() throws StorageException {
		for (Video video : elements)
			delete(video);
		log.debug("delete all current space {}/{}", space, capacity);
	}

	@Override
	public boolean contains(Video e) {
		return elements.contains(e);
	}

	@Override
	public int size() {
		return elements.size();
	}

	@Override
	public long getTotalSpace() {
		return space;
	}

	@Override
	public long getFreeSpace() {
		return capacity > 0 ? capacity - space : -1;
	}

	@Override
	public long getUsableSpace() {
		return space;
	}

	@Override
	public Video get(int index) {
		if (elements.size() < index)
			throw new StorageException(String.format("index over : %s < %s", elements.size(), index));
		return elements.get(index);
	}

	@Override
	public Video get(String key) {
		for (Video video : elements) {
			if (StringUtils.equalsIgnoreCase(video.getName(), key))
				return video;
		}
		throw new StorageException(String.format("Notfound %s", key));
	}

	@Override
	public List<Video> find(String query) {
		List<Video> found = new ArrayList<Video>();
		for (Video video : elements) {
			if (StringUtils.containsIgnoreCase(video.getQuery(), query)) {
				found.add(video);
			}
		}
		log.debug("find {} by {}", found.size(), query);
		return found;
	}

	private boolean checkCapacity(long length) {
		if (capacity < 0)
			return true;
		if (capacity > space + length)
			return true;
		else
			throw new StorageException(String.format("Storage capacity over : %s > %s + %s", capacity, space, length));
	}

	@Override
	public void destory() {
		deleteAll();
	}

	@Override
	public String getName() {
		return name;
	}

}
