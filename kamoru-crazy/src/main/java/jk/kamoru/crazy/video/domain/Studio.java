package jk.kamoru.crazy.video.domain;

import java.io.File;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import jk.kamoru.crazy.video.VIDEO;
import jk.kamoru.crazy.video.util.VideoUtils;
import jk.kamoru.util.FileUtils;
import jk.kamoru.util.StringUtils;
import lombok.Data;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("prototype")
@Data
@XmlRootElement(name = "studio", namespace = "http://www.w3.org/2001/XMLSchema-instance")
@XmlAccessorType(XmlAccessType.FIELD)
public class Studio implements Serializable, Comparable<Studio> {

	private static final long serialVersionUID = VIDEO.SERIAL_VERSION_UID;

	@Value("#{prop['video.basePath']}") 		private String[] basePath;

	private String name;
	private URL    homepage;
	private String company;

	@XmlTransient
	@JsonIgnore
	private List<Video> videoList;
	@XmlTransient
	@JsonIgnore
	private List<Actress> actressList;

	private boolean loaded;

	private StudioSort sort = StudioSort.NAME;
	
	public Studio() {
		videoList = new ArrayList<Video>();
		actressList = new ArrayList<Actress>();
	}

	public Studio(String name) {
		this();
		this.name = name;
	}
	
	@Override
	public String toString() {
		return String.format("%s Score %s %s %s",
				name, getScore(), StringUtils.trimToEmpty(homepage), StringUtils.trimToEmpty(company));
	}

	public void addVideo(Video video) {
		if(!videoList.contains(video))
			this.videoList.add(video);		
	}
	public void addActress(Actress actress) {
		boolean found = false;
		for(Actress actressInList : this.actressList) {
			if(actressInList.getName().equalsIgnoreCase(actress.getName())) {
				actressInList = actress;
				found = true;
				break;
			}
		}
		if(!found)
			this.actressList.add(actress);
	}
	
	public URL getHomepage() {
		loadInfo();
		return homepage;
	}
	public String getCompany() {
		loadInfo();
		return company;
	}

	@Override
	public int compareTo(Studio comp) {
		switch (sort) {
		case NAME:
			return StringUtils.compareToIgnoreCase(this.getName(), comp.getName());
		case HOMEPAGE:
			return StringUtils.compareTo(this.getHomepage(), comp.getHomepage());
		case COMPANY:
			return StringUtils.compareToIgnoreCase(this.getCompany(), comp.getCompany());
		case VIDEO:
			return this.getVideoList().size() - comp.getVideoList().size();
		case SCORE:
			return this.getScore() - comp.getScore();
		default:
			return StringUtils.compareToIgnoreCase(this.getName(), comp.getName());
		}
	}

	private void loadInfo() {
		if (!loaded) {
			Map<String, String> info = VideoUtils.readFileToMap(new File(basePath[0], name + FileUtils.EXTENSION_SEPARATOR + VIDEO.EXT_STUDIO));
			try {
				this.homepage = new URL(info.get("HOMEPAGE"));
			} catch (MalformedURLException e) {
				// do nothing!
			}
			this.company = info.get("COMPANY");
			loaded = true;
		}
	}
	public void reloadInfo() {
		loaded = false;
	}

	public void emptyVideo() {
		videoList.clear();
	}

	/**
	 * sum of video scoring in studio
	 * @return
	 */
	public int getScore() {
		int score = 0;
		for (Video video : getVideoList())
			score += video.getScore();
		return score;
	}
	
	
}
