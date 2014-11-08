package jk.kamoru.crazy.video.domain;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import jk.kamoru.core.storage.Storage;
import jk.kamoru.crazy.video.VIDEO;
import jk.kamoru.crazy.video.VideoException;
import jk.kamoru.crazy.video.service.HistoryService;
import jk.kamoru.crazy.video.source.FileBaseVideoSource;
import jk.kamoru.crazy.video.util.VideoUtils;
import jk.kamoru.util.FileUtils;
import jk.kamoru.util.StringUtils;
import net.sf.json.JSONObject;

import org.apache.commons.io.FileExistsException;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * AV Bean class<br>
 * include studio, opus, title, overview info and video, cover, subtitles, log file<br>
 * action play, random play, editing subtitles and overview  
 * @author kamoru
 *
 */
@Component
@Scope("prototype")
@XmlRootElement(name = "video", namespace = "http://www.w3.org/2001/XMLSchema-instance")
@XmlAccessorType(XmlAccessType.FIELD)
public class Video implements Comparable<Video>, Serializable, Storage.Element {

	private static final long serialVersionUID = VIDEO.SERIAL_VERSION_UID;

	private static final Logger logger = LoggerFactory.getLogger(Video.class);
	
	private static Sort sortMethod = VIDEO.DEFAULT_SORTMETHOD;
	
	@XmlTransient
	@JsonIgnore
	@Autowired HistoryService historyService;
	
	// files
	private List<File> videoFileList;
	private List<File> subtitlesFileList;
	private File coverFile;
	private File coverWebpFile;
	private File infoFile; // json file
	private List<File> etcFileList;
	private List<File> videoCandidates;

	// info
	@XmlTransient
	@JsonIgnore
	private Studio studio;
	private String opus;
	private String title;
	private String overview; // overview text
	private String etcInfo;
	private String releaseDate;
	@XmlTransient
	@JsonIgnore
	private List<Actress> actressList;
//	private List<History> historyList; // history list
	private Integer playCount;
	private int rank; // ranking score

	@Value("#{prop['score.ratio.rank']}")		private int      rankRatio;
	@Value("#{prop['score.ratio.play']}")		private int      playRatio;
	@Value("#{prop['score.ratio.actress']}")	private int   actressRatio;
	@Value("#{prop['score.ratio.subtitles']}")	private int subtitlesRatio;
//	@Value("#{prop['score.ratio.unseen']}")		private int    unseenRatio;


	public Video() {
		videoFileList 		= new ArrayList<File>();
		subtitlesFileList 	= new ArrayList<File>();
		etcFileList 		= new ArrayList<File>();
		videoCandidates		= new ArrayList<File>();
		
		actressList = new ArrayList<Actress>();
//		historyList = new ArrayList<History>();

		playCount 	= 0;
		rank 		= 0;
		overview 	= "";
	}
	
	/**
	 * 비디오 파일이 있으면, 나머지 파일을 같은 위치에 모은다.
	 */
	public void arrange() {
		logger.trace(opus);
		if (this.isExistVideoFileList()) {
			move(this.getDelegatePath());
		}
	}

	@Override
	public int compareTo(Video comp) {
		switch(sortMethod) {
		case S:
			return StringUtils.compareToIgnoreCase(this.getStudio().getName(), comp.getStudio().getName());
		case O:
			return StringUtils.compareToIgnoreCase(this.getOpus(), comp.getOpus());
		case T:
			return StringUtils.compareToIgnoreCase(this.getTitle(), comp.getTitle());
		case A:
			return StringUtils.compareToIgnoreCase(this.getActressName(), comp.getActressName());
		case M:
			return this.getDelegateFile().lastModified() > comp.getDelegateFile().lastModified() ? 1 : -1;
		case P:
			return this.getPlayCount() - comp.getPlayCount();
		case R:
			return this.getRank() - comp.getRank();
		case L:
			return this.getLength() > comp.getLength() ? 1 : -1;
		case SC:
			return this.getScore() - comp.getScore();
		default:
			return StringUtils.compareTo(this, comp);
		}
	}
	
	/**
	 * actress 이름이 있는지 확인
	 * @param actressName
	 * @return 같은 이름이 있으면 {@code true}
	 */
	public boolean containsActress(String actressName) {
		for(Actress actress : actressList)
			if (VideoUtils.equalsName(actress.getName(), actressName))
				return true;
		return false;
	}
	
	/**
	 * 전체 배우 이름을 컴마(,)로 구분한 문자로 반환
	 * @return 배우이름 문자열
	 */
	public String getActressName() {
		List<String> list = new ArrayList<String>();
		for(Actress actress : actressList) {
			list.add(actress.getName());
		}
		return VideoUtils.toListToSimpleString(list);
	}

	/**
	 * Actress 목록. 이름순 정렬
	 * @return actress list by name sort
	 */
	public List<Actress> getActressList() {
		Collections.sort(actressList);
		return actressList;
	}
	
	/**
	 * cover file의 byte[] 반환
	 * @return 없거나 에러이면 null 반환
	 */
	@JsonIgnore
	public byte[] getCoverByteArray() {
		return VideoUtils.readFileToByteArray(coverFile);
	}
	
	/**
	 * 커버 파일 반환
	 * @return 커버 파일
	 */
	public File getCoverFile() {
		return coverFile;
	}

	/**
	 * cover 파일 절대 경로
	 * @return 없으면 공백
	 */
	public String getCoverFilePath() {
		if(isExistCoverFile())
			return getCoverFile().getAbsolutePath();
		return "";
	}
	
	/**
	 * webp형식의 cover file의 byte[] 반환
	 * @return 없거나 에러이면 null 반환
	 */
	@JsonIgnore
	public byte[] getCoverWebpByteArray() {
		return VideoUtils.readFileToByteArray(coverWebpFile);
	}

	/**
	 * WebP cover 파일 반환
	 * @return 커버 파일 WebP
	 */
	public File getCoverWebpFile() {
		return coverWebpFile;
	}
	
	/**
	 * WebP cover 파일 절대 경로
	 * @return 없으면 공백
	 */
	public String getCoverWebpFilePath() {
		if (this.isExistCoverWebpFile())
			return this.getCoverWebpFile().getAbsolutePath();
		return "";
	}
	
	/**
	 * video 대표 파일
	 * @return 대표 파일
	 */
	private File getDelegateFile() {
		if (this.isExistVideoFileList()) 
			return this.getVideoFileList().get(0);
		else if (this.isExistCoverFile()) 
			return this.getCoverFile();
		else if (this.isExistSubtitlesFileList()) 
			return this.getSubtitlesFileList().get(0);
		else if (this.isExistEtcFileList()) 
			return this.getEtcFileList().get(0);
		else if (this.infoFile != null) 
			return this.infoFile;
		else if (this.coverWebpFile != null)
			return this.coverWebpFile;
		else 
			throw new VideoException(this, "No delegate video file : " + this.opus + " " + this.toString());
	}

	/**
	 * video 대표 파일 이름. 확장자를 뺀 대표이름
	 * @return 대표 이름
	 */
	private String getDelegateFilenameWithoutSuffix() {
		return FileUtils.getNameExceptExtension(getDelegateFile());
	}
	
	/**
	 * video 대표 폴더 경로. video > cover > overview > subtitles > etc 순으로 찾는다.
	 * @return 대표 경로
	 */
	public String getDelegatePath() {
		return this.getDelegateFile().getParent();
	}
	
	/**
	 * video 대표 폴더 경로. video > cover > overview > subtitles > etc 순으로 찾는다.
	 * @return 대표 경로 file
	 */
	public File getDelegatePathFile() {
		return this.getDelegateFile().getParentFile();
	}

	/**
	 * 기타 파일 
	 * @return etc file list
	 */
	public List<File> getEtcFileList() {
		return etcFileList;
	}
	
	/**
	 * 기타 파일 경로
	 * @return string of etc file list
	 */
	public String getEtcFileListPath() {
		if(isExistEtcFileList())
			return VideoUtils.arrayToString(getEtcFileList());
		return "";
	}
	
	/**기타 정보. 날짜
	 * @return etc info
	 */
	public String getEtcInfo() {
		return etcInfo;
	}

	/**
	 * 모든 파일 list. null도 포함 되어 있을수 있음
	 * @return all file
	 */
	public List<File> getFileAll() {
		List<File> list = new ArrayList<File>();
		list.addAll(getVideoFileList());
		list.addAll(getSubtitlesFileList());
		list.addAll(getEtcFileList());
		list.add(this.coverFile);
		list.add(this.coverWebpFile);
		list.add(this.infoFile);
		return list;
	}
	
	/**
	 * info 파일 반환. 없으면 대표경로에 만듬.
	 * @return info
	 */
	public File getInfoFile() {
		if(this.infoFile == null) {
			this.infoFile = new File(this.getDelegatePath(), this.getDelegateFilenameWithoutSuffix() + FileUtils.EXTENSION_SEPARATOR + VIDEO.EXT_INFO);
			try {
				this.infoFile.createNewFile();
			} catch (IOException e) {
				logger.error("fail to create info file", e);
			}
		}
		return infoFile;
	}

	/**
	 * info 파일 경로
	 * @return info path
	 */
	public String getInfoFilePath() {
		return getInfoFile().getAbsolutePath();
	}

	/**품번
	 * @return 품번
	 */
	public String getOpus() {
		return opus;
	}

	/**
	 * overview
	 * @return overvire text
	 */
	public String getOverviewText() {
		return overview;
	}

	/**
	 * play count
	 * @return count of play
	 */
	public Integer getPlayCount() {
		return playCount;
	}

	/**
	 * rank point
	 * @return rank
	 */
	public int getRank() {
		return rank;
	}

	/**
	 * studio
	 * @return studio class
	 */
	public Studio getStudio() {
		return studio;
	}

	/**
	 * subtitles file list
	 * @return list of subtitles file
	 */
	public List<File> getSubtitlesFileList() {
		return subtitlesFileList;
	}

	/**
	 * subtitles file list path
	 * @return string of sutitles path
	 */
	public String getSubtitlesFileListPath() {
		if(isExistSubtitlesFileList())
			return VideoUtils.arrayToString(getSubtitlesFileList());
		return "";
	}

	/**
	 * 자막 파일 위치를 배열로 반환, 외부 에디터로 자막 수정시 사용 
	 * @return array of subtitles oath
	 */
	public String[] getSubtitlesFileListPathArray() {
		if(isExistSubtitlesFileList()) {
			String[] filePathes = new String[this.subtitlesFileList.size()];
			for(int i=0; i<this.subtitlesFileList.size(); i++)
				filePathes[i] = this.subtitlesFileList.get(i).getAbsolutePath();
			return filePathes;
		}
		return null;
	}
	
	/**
	 * video title
	 * @return title
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * video의 대표 날자. video > cover > overview > subtitles > etc 순으로 찾는다.
	 * @return date of video
	 */
	public String getVideoDate() {
		return DateFormatUtils.format(this.getDelegateFile().lastModified(), "yyyy-MM-dd");
	}

	/**
	 * video file list
	 * @return list of video file
	 */
	public List<File> getVideoFileList() {
		return videoFileList;
	}

	/**
	 * video file list path
	 * @return string of video path
	 */
	public String getVideoFileListPath() {
		if(isExistVideoFileList()) 
			return VideoUtils.arrayToString(getVideoFileList()); 
		return "";
	}
	
	/**
	 * 비디오 파일 목록 배열. 플레이어 구동시 사용
	 * @return array of video path
	 */
	public String[] getVideoFileListPathArray() {
		if(isExistVideoFileList()) {
			String[] filePathes = new String[getVideoFileList().size()];
			for(int i=0; i<filePathes.length; i++)
				filePathes[i] = getVideoFileList().get(i).getAbsolutePath();
			return filePathes;
		}
		return null;
	}

	/**비디오 상대 경로 형식의 URL객체
	 * @return url of video
	 */
	public URL getVideoURL() {
		if (videoFileList == null || videoFileList.size() == 0) {
			return null;
		}
		else {
			File vfile = videoFileList.get(0);
			String pname = vfile.getParentFile().getName();
			
			try {
				return new URL("/" + pname + "/" + vfile.getName());
			} catch (MalformedURLException e) {
				logger.warn("Error: {}", e.getMessage());
			}
			return null;
		}
	}
	
	/**
	 * play count 증가
	 */
	public void increasePlayCount() {
		this.playCount++;
		this.saveInfo();
	}

	/**
	 * 커버 파일이 존재하는지
	 * @return {@code true} if exist
	 */
	public boolean isExistCoverFile() {
		return this.coverFile != null;
	}

	/**
	 * WebP 커버 파일이 존재하는지
	 * @return {@code true} if exist
	 */
	public boolean isExistCoverWebpFile() {
		return this.coverWebpFile != null;
	}

	/**
	 * 기타 파일이 존재하는지
	 * @return {@code true} if exist
	 */
	public boolean isExistEtcFileList() {
		return this.etcFileList != null && this.etcFileList.size() > 0;
	}

	/**
	 * overview가 있는지
	 * @return {@code true} if exist
	 */
	public boolean isExistOverview() {
		return this.overview != null && this.overview.trim().length() > 0;
	}

	/**
	 * 자막 파일이 존재하는지
	 * @return {@code true} if exist
	 */
	public boolean isExistSubtitlesFileList() {
		return this.subtitlesFileList != null && this.subtitlesFileList.size() > 0;
	}

	/**
	 * 비디오 파일이 존재하는지
	 * @return {@code true} if exist
	 */
	public boolean isExistVideoFileList() {
		return this.videoFileList != null && this.videoFileList.size() > 0;  
	}

	/**
	 * destDir 폴더로 전체 파일 이동
	 * @param destDir
	 */
	public void move(String destDir) {
		File destFile = new File(destDir);
		if (!destFile.exists()) 
			throw new VideoException(this, "directory(" + destDir + ") is not exist!");
		for (File file : getFileAll()) {
			if (file != null && file.exists() && !file.getParent().equals(destDir)) {
				if (destFile.getFreeSpace() < file.length()) {
					logger.warn("destination is small. size=" + destFile.getFreeSpace());
					break;
				}
				try {
					logger.debug("attempt to move file from {} to {}", file.getAbsolutePath(), destDir);
					FileUtils.moveFileToDirectory(file, destFile, false);
				} catch (FileExistsException fe) {
					logger.warn("File exist, then delete ", fe);
					FileUtils.deleteQuietly(file);
				} catch (IOException e) {
					logger.error("Fail move file", e);
				}
			}
		}
	}

	/**
	 * actress를 추가한다. 기존actress가 발견되면 ref를 갱신.
	 * @param actress
	 */
	public void addActress(Actress actress) {
		boolean notFound = true;
		for (Actress actressInList : this.actressList) {
			if (actressInList.contains(actress.getName())) {
				notFound = false;
				actressInList = actress;
				break;
			}
		}
		if (notFound)
			this.actressList.add(actress);
	}

	/**
	 * 모든 파일을 지운다.
	 */
	public void removeVideo() {
		for(File file : getFileAll())
			if(file != null && file.exists()) 
				if(FileUtils.deleteQuietly(file))
					logger.debug(file.getAbsolutePath());
				else
					logger.error("delete fail : {}", file.getAbsolutePath());
	}

	/**
	 * info 내용 저장
	 */
	private void saveInfo() {
		JSONObject root = new JSONObject();
		JSONObject info = new JSONObject();

		info.put("opus", this.opus);
		info.put("rank", this.rank);
		info.put("playCount", this.playCount);
		info.put("overview", this.overview);
		root.put("info", info);

		try {
			FileUtils.writeStringToFile(getInfoFile(), root.toString(), VIDEO.FILE_ENCODING);
			logger.info("{} {}", opus, root.toString());
		} catch (IOException e) {
			logger.error("info save error", e);
		}
	}

	/**
	 * overview 내용 저장
	 * @param overViewText
	 */
	public void saveOverView(String overViewText) {
		logger.trace("{} [{}]", opus, overViewText);
		this.overview = overViewText;
		this.saveInfo();
	}
	
	/**
	 * actress list
	 * @param actressList
	 */
	public void setActressList(List<Actress> actressList) {
		this.actressList = actressList;
	}

	/**
	 * cover file
	 * @param coverFile
	 */
	public void setCoverFile(File coverFile) {
		this.coverFile = coverFile;
	}

	/**
	 * webp cover file
	 * @param coverWebpFile
	 */
	public void setCoverWebpFile(File coverWebpFile) {
		this.coverWebpFile = coverWebpFile;
	}

	/**
	 * etc file
	 * @param file
	 */
	public void addEtcFile(File file) {
		this.etcFileList.add(file);		
	}
	
	/**
	 * etc file list
	 * @param etcFileList
	 */
	public void setEtcFileList(List<File> etcFileList) {
		this.etcFileList = etcFileList;
	}
	
	/**
	 * etc info
	 * @param etcInfo
	 */
	public void setEtcInfo(String etcInfo) {
		this.etcInfo = etcInfo;
	}

	/**
	 * info file. 파일 분석해서 필요 데이터(rank, overview, history, playcount) 설정
	 * @param file info file
	 */
	public void setInfoFile(File file) {
		this.infoFile = file;
		
		JSONObject json = null;
		try {
			String infoText = FileUtils.readFileToString(infoFile, VIDEO.FILE_ENCODING);
			if (infoText != null && infoText.trim().length() > 0)
				json = JSONObject.fromObject(infoText);
			else 
				return;
		} catch (Exception e1) {
			logger.error("info read error : {} - {}", this.opus, e1);
			return;
		}
		JSONObject infoData = json.getJSONObject("info");

		String opus = infoData.getString("opus");
		if (!this.opus.equalsIgnoreCase(opus)) 
			throw new VideoException(this, "invalid info file. " + this.opus + " != " + opus);
		
		this.rank 		= infoData.getInt("rank");
		try {
			this.playCount 	= infoData.getInt("playCount");
		}
		catch (Exception e) {
//			int play = 0;
//			for(History history : historyService.findByOpus(this.opus)) {
//				if (history.getAction() == Action.PLAY)
//					play++;
//			}
//			if (playCount != play) {
//				logger.info("infoFile.playCount[{}] != history.play[{}]", this.playCount, play);
//				if (playCount < play)
//					playCount = play;
//			}
		} 
		this.overview 	= infoData.getString("overview");

	}

	/**
	 * opus
	 * @param opus
	 */
	public void setOpus(String opus) {
		this.opus = opus;
	}

	/**
	 * play count
	 * @param playCount
	 */
	public void setPlayCount(Integer playCount) {
		this.playCount = playCount;
		this.saveInfo();
	}

	/**
	 * rank. info 파일에 저장
	 * @param rank
	 */
	public void setRank(int rank) {
		logger.trace("{} rank is {}", opus, rank);
		this.rank = rank;
		this.saveInfo();
	}

	/**
	 * sort method. 정렬 방식 
	 * @param sortMethod
	 */
	@SuppressWarnings("static-access")
	public void setSortMethod(Sort sortMethod) {
		this.sortMethod = sortMethod;
	}

	/**
	 * studio
	 * @param studio
	 */
	public void setStudio(Studio studio) {
		this.studio = studio;
	}

	/**
	 * add subtitles file
	 * @param file
	 */
	public void addSubtitlesFile(File file) {
		this.subtitlesFileList.add(file);
	}
	
	/**
	 * subtitles file list
	 * @param subtitlesFileList
	 */
	public void setSubtitlesFileList(List<File> subtitlesFileList) {
		this.subtitlesFileList = subtitlesFileList;
	}
	
	/**
	 * title
	 * @param title
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * add video file
	 * @param file
	 */
	public void addVideoFile(File file) {
		this.videoFileList.add(file);
	}

	/**
	 * video file list
	 * @param videoFileList
	 */
	public void setVideoFileList(List<File> videoFileList) {
		this.videoFileList = videoFileList;
	}
	
	public String getReleaseDate() {
		return releaseDate;
	}

	public void setReleaseDate(String releaseDate) {
		this.releaseDate = releaseDate;
	}

	@Override
	public String toString() {
		return String
				.format("Video [opus=%s, actressList=%s, coverFile=%s, coverWebpFile=%s, etcFileList=%s, etcInfo=%s, infoFile=%s, overview=%s, playCount=%s, rank=%s, studio=%s, subtitlesFileList=%s, title=%s, videoFileList=%s, releaseDate=%s]",
						opus, actressList, coverFile, coverWebpFile, etcFileList,
						etcInfo, infoFile, overview,
						playCount, rank, studio, subtitlesFileList, title,
						videoFileList, releaseDate);
	}

	/**
	 * video의 모든 파일 크기
	 * @return entire length of video
	 */
	@Override
	public long getLength() {
		long length = 0l;
		for (File file : this.getFileAll()) {
			if (file != null)
				length += file.length();
		}
		return length;
	}
	
	/**video full name
	 * @return [studio][opus][title][actress][date]
	 */
	public String getFullname() {
		return String.format("[%s][%s][%s][%s][%s]", studio.getName(), opus, title, getActressName(), StringUtils.isEmpty(releaseDate) ? getVideoDate() : releaseDate);
	}
	
	/**비디오 점수<br>
	 * rank, playCount, actress video count, subtitles count
	 * @return score
	 */
	public int getScore() {
		if (getPlayScore() == 0)
			return 0;
		else
			return getRankScore() + getPlayScore() + getActressScore() + getSubtitlesScore();
	}

	/**자세한 비디오 점수
	 * @return 비디오 점수 설명 
	 */
	public String getScoreDesc() {
		return String.format("Rank[%s]*%s + Play[%s]*%s + Actress[%s]*%s + Subtitles[%s]*%s = %s", 
				getRank(), rankRatio,
				getPlayCount(), playRatio,
				getActressScoreDesc(), actressRatio,
				isExistSubtitlesFileList() ? 1 : 0, subtitlesRatio,
				getScore());
	}
	
	public String getScoreRatio() {
		return String.format("Score ratio {Rank[%s] Play[%s] Actress[%s] Subtitles[%s]}", rankRatio, playRatio, actressRatio, subtitlesRatio);
	}
	/**환산된 랭킹 점수
	 * @return score of rank
	 */
	public int getRankScore() {
		return getRank() * rankRatio;
	}

	/**환산된 플레이 점수
	 * @return score of play count
	 */
	public int getPlayScore() {
		return getPlayCount() * playRatio;
	}

	/**환산된 배우 점수
	 * @return score of actress
	 */
	public int getActressScore() {
		int actressVideoScore = 0;
		if (getActressList().size() == 1 
				&& getActressList().get(0).getName().equals(FileBaseVideoSource.unclassifiedActress))
			return actressVideoScore;
			
		for (Actress actress : getActressList()) {
			actressVideoScore += actress.getVideoList().size() * actressRatio;
		}
		return actressVideoScore;
	}
	/**여배우 점수 설명
	 * @return description of actress score
	 */
	public String getActressScoreDesc() {
		String desc = "";

		if (getActressList().size() == 1 
				&& getActressList().get(0).getName().equals(FileBaseVideoSource.unclassifiedActress))
			return FileBaseVideoSource.unclassifiedActress;
		
		boolean first = true;
		for (Actress actress : getActressList()) {
			desc += first ? "" : "+";
			if (!actress.getName().equals(FileBaseVideoSource.unclassifiedActress))
				desc += actress.getVideoList().size();
			
			first = false;
		}
		return desc;
	}
	/**환산된 자막 점수
	 * @return score of subtitles
	 */
	public int getSubtitlesScore() {
		return (isExistSubtitlesFileList() ? 1 : 0) * subtitlesRatio;
	}

	/**비디오 파일 후보 추가
	 * @param file
	 */
	public void addVideoCandidates(File file) {
		videoCandidates.add(file);
	}

	/**비디오 파일 후보 getter
	 * @return list of the videoCandidates
	 */
	public List<File> getVideoCandidates() {
		return videoCandidates;
	}

	/**비디오 파일 후보 setter
	 * @param videoCandidates the videoCandidates to set
	 */
	public void setVideoCandidates(List<File> videoCandidates) {
		this.videoCandidates = videoCandidates;
	}

	/**
	 * 비디오 파일 후보 list clear
	 */
	public void resetVideoCandidates() {
		this.videoCandidates.clear();
	}

	public void rename(String newName) {
		int count = 1;
		// video
		for (File file : VideoUtils.sortFile(getVideoFileList())) {
			FileUtils.rename(file, newName + count++);
		}
		// cover
		FileUtils.rename(coverFile, newName);
		FileUtils.rename(coverWebpFile, newName);
		// subtitles, if exist
		count = 1;
		for (File file : VideoUtils.sortFile(getSubtitlesFileList())) {
			FileUtils.rename(file, newName + count++);
		}
		// info file
		FileUtils.rename(infoFile, newName);
		// etc file
		for (File file : this.getEtcFileList()) {
			FileUtils.rename(file, newName);
		}
	}

	public void renameOfActress(String newName) {
		String newVideoName = String.format("[%s][%s][%s][%s][%s]", studio.getName(), opus, title, newName, StringUtils.isEmpty(releaseDate) ? getVideoDate() : releaseDate);
		rename(newVideoName);
	}

	public void renameOfStudio(String newName) {
		rename(String.format("[%s][%s][%s][%s][%s]", newName, opus, title, getActressName(), StringUtils.isEmpty(releaseDate) ? getVideoDate() : releaseDate));
	}

	@Override
	public String getName() {
		return getOpus();
	}

	@Override
	public String getQuery() {
		return getFullname();
	}

	@Override
	public void delete() {
		removeVideo();
	}
	
	/**대표 파일의 확장자
	 * @return
	 */
	public String getExt() {
		return FileUtils.getExtension(getDelegateFile());
	}
}
