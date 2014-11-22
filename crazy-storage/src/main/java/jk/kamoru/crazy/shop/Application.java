package jk.kamoru.crazy.shop;

import java.net.InetAddress;
import java.net.UnknownHostException;

import jk.kamoru.crazy.CRAZY;
import jk.kamoru.crazy.domain.Actress;
import jk.kamoru.crazy.domain.Studio;
import jk.kamoru.crazy.domain.Video;
import jk.kamoru.crazy.service.CrazyShop;
import jk.kamoru.crazy.shop.source.ref.FileBaseVideoSource;
import jk.kamoru.crazy.shop.source.ref.ImageSource;
import jk.kamoru.crazy.shop.source.ref.LocalImageSource;
import jk.kamoru.crazy.shop.source.ref.VideoSource;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.Scope;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.remoting.rmi.RmiServiceExporter;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@Configuration
@ComponentScan
@EnableAutoConfiguration
@PropertySource("classpath:/crazy.kamoru-mac.local.properties")
public class Application {

    public static void main(String[] args) throws UnknownHostException {
        SpringApplication.run(Application.class, args);
        String hostName = InetAddress.getLocalHost().getHostName();
        System.setProperty("hostName", hostName);
    }

//  TODO change  @Value("#{'${my.list.of.strings}'.split(',')}") -> private List<String> myList;
    @Value("${path.storage.video}") 	private String[] videoStoragePaths;
    @Value("${path.storage.image}")	 	private String[] imageStoragePaths;
    @Value("${extension.video}") 		private String videoExtensions;
    @Value("${extension.cover}") 		private String coverExtensions;
    @Value("${extension.subtitles}") 	private String subtitlesExtensions;

	@Value("${score.ratio.rank}")		private int      rankRatio;
	@Value("${score.ratio.play}")		private int      playRatio;
	@Value("${score.ratio.actress}")	private int   actressRatio;
	@Value("${score.ratio.subtitles}")	private int subtitlesRatio;

    @Bean
    @Scope("prototype")
    public Video video() {
    	Video video = new Video();
    	video.setRankRatio(rankRatio);
    	video.setPlayRatio(playRatio);
    	video.setActressRatio(actressRatio);
    	video.setSubtitlesRatio(subtitlesRatio);
    	return video;
    }
    
    @Bean
    @Scope("prototype")
    public Studio studio() {
    	Studio studio = new Studio();
    	studio.setVideoStoragePaths(videoStoragePaths);
    	return studio;
    }
    
    @Bean
    @Scope("prototype")
    public Actress actress() {
    	Actress actress = new Actress();
    	actress.setVideoStoragePaths(videoStoragePaths);
    	return actress;
    }
    
    @Bean
    public static PropertySourcesPlaceholderConfigurer placeholderConfigurer() {
      return new PropertySourcesPlaceholderConfigurer();
    }
    
    @Bean
    public CrazyShop storageSevice() {
    	return new CrazyJKShop();
    }
    
    @Bean
    public RmiServiceExporter storageExporter() {
    	RmiServiceExporter rmiServiceExporter = new RmiServiceExporter();
    	rmiServiceExporter.setService(storageSevice());
    	rmiServiceExporter.setServiceName("StorageService");
    	rmiServiceExporter.setServiceInterface(CrazyShop.class);
    	rmiServiceExporter.setRegistryPort(CRAZY.RMI_PORT_STORAGE);
    	return rmiServiceExporter;
    }

    @Bean
    public VideoSource videoSource() {
    	FileBaseVideoSource videoSource = new FileBaseVideoSource();
    	videoSource.setVideoStoragePaths(videoStoragePaths);
    	videoSource.setVideoExtensions(videoExtensions);
    	videoSource.setCoverExtensions(coverExtensions);
    	videoSource.setSubtitlesExtensions(subtitlesExtensions);
    	return videoSource;
    }
    
    @Bean
    public ImageSource imageSource() {
    	LocalImageSource imageSource = new LocalImageSource();
    	imageSource.setImageStoragePaths(imageStoragePaths);
    	return imageSource;
    }
}
