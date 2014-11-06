package jk.kamoru.crazy.video.source.storage;

import java.util.Hashtable;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.springframework.stereotype.Component;

import jk.kamoru.core.storage.Storage;
import jk.kamoru.core.storage.StorageException;
import jk.kamoru.core.storage.StorageFacilities;
import jk.kamoru.crazy.video.domain.Video;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class VideoStorageFacilities implements StorageFacilities<Video> {

	private Map<String, Storage<Video>> storages = new Hashtable<String, Storage<Video>>();
	
	@Override
	public Storage<Video> get(int index) {
		return get(getName(index));
	}

	@Override
	public Storage<Video> get(String name) {
		if (storages.containsKey(name))
			return storages.get(name);
		throw new StorageException(String.format("Notfound %s", name));
	}

	@Override
	public void moveTo(Video video, Storage<Video> from, Storage<Video> to) {
		from.remove(video);
		to.add(video);
	}

	@Override
	public void moveTo(Video video, String from, String to) {
		moveTo(video, get(from), get(to));
	}

	@Override
	public void moveTo(Video video, int from, int to) {
		moveTo(video, get(from), get(to));
	}

	@Override
	public void delete(int index) {
		storages.get(getName(index)).deleteAll();
		storages.remove(getName(index));
	}

	@Override
	public void delete(String name) {
		storages.get(name).deleteAll();
		storages.remove(name);
	}

	@Override
	public void delete(Storage<Video> storage) {
		storages.get(getName(storage)).deleteAll();
		storages.remove(getName(storage));
	}

	@Override
	public void remove(int index) {
		remove(getName(index));
	}

	@Override
	public void remove(String name) {
		storages.remove(name);
	}

	@Override
	public void remove(Storage<Video> storage) {
		remove(getName(storage));
	}

	private String getName(int index) {
		if (storages.size() > index)
			throw new StorageException(String.format("index over %s < %s", storages.size(), index));
		int idx = 0;
		for (String name : storages.keySet())
			if (index == idx++)
				return name;
		throw new StorageException(String.format("Notfound %s", index));
	}
	
	private String getName(Storage<Video> storage) {
		for (Entry<String, Storage<Video>> entry : storages.entrySet())
			if (entry.getValue().equals(storage))
				return entry.getKey();
		throw new StorageException(String.format("Notfound %s", storage));
	}

	@Override
	public void deleteAll() {
		for (String name : nameSet())
			delete(name);
		log.debug("delete all Storage");
	}

	@Override
	public void removeAll() {
		for (String name : nameSet())
			remove(name);
		log.debug("remove all Storage");
	}

	@Override
	public Set<String> nameSet() {
		return storages.keySet();
	}

	@Override
	public boolean contains(String name) {
		return storages.containsKey(name);
	}

	@Override
	public boolean contains(Storage<Video> storage) {
		return storages.containsValue(storage);
	}

	@Override
	public Storage<Video> create(String name) throws StorageException {
		return create(name, -1l);
	}

	@Override
	public Storage<Video> create(String name, long capacity) throws StorageException {
		if (contains(name))
			throw new StorageException(String.format("duplicate name %s", name));
		return VideoStorage.newStorage(name, capacity);
	}
	
}