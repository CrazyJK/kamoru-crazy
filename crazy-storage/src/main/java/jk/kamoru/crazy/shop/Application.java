package jk.kamoru.crazy.shop;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;

import jk.kamoru.crazy.CRAZY;
import jk.kamoru.crazy.domain.Actress;
import jk.kamoru.crazy.domain.Studio;
import jk.kamoru.crazy.domain.Video;
import jk.kamoru.crazy.service.CrazyShop;
import jk.kamoru.crazy.shop.harvester.FileHarvester;
import jk.kamoru.crazy.shop.harvester.Harvester;

import org.springframework.beans.factory.annotation.Autowired;
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
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.servlet.configuration.EnableWebMvcSecurity;

@EnableScheduling
@Configuration
@ComponentScan
@EnableWebMvcSecurity
@EnableAutoConfiguration
@PropertySource("classpath:/crazy.kamoru-mac.local.properties")
public class Application extends WebSecurityConfigurerAdapter {

    public static void main(String[] args) throws UnknownHostException {
        SpringApplication.run(Application.class, args);
        String hostName = InetAddress.getLocalHost().getHostName();
        System.setProperty("hostName", hostName);
    }

    @Value("#{'${path.storage.video}'.split(',')}") private List<String> videoStoragePaths;
    @Value("#{'${path.storage.image}'.split(',')}")	private List<String> imageStoragePaths;

	@Value("${score.ratio.rank}")		private int      rankRatio;
	@Value("${score.ratio.play}")		private int      playRatio;
	@Value("${score.ratio.actress}")	private int   actressRatio;
	@Value("${score.ratio.subtitles}")	private int subtitlesRatio;

    @Bean
    public static PropertySourcesPlaceholderConfigurer placeholderConfigurer() {
      return new PropertySourcesPlaceholderConfigurer();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
            .authorizeRequests()
                .antMatchers("/", "/video", "/video/list").permitAll()
                .anyRequest().authenticated()
                .and()
            .formLogin()
                .loginPage("/login")
                .permitAll()
                .and()
            .logout()
                .permitAll();
    }
    
    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth
            .inMemoryAuthentication()
                .withUser("kamoru").password("crazyjk").roles("USER");
    }
    
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
//    	studio.setVideoStoragePaths(videoStoragePaths);
    	return studio;
    }
    
    @Bean
    @Scope("prototype")
    public Actress actress() {
    	Actress actress = new Actress();
//    	actress.setVideoStoragePaths(videoStoragePaths);
    	return actress;
    }
    
    @Bean
    public CrazyShop crazyShop() {
    	return new CrazyJKShop();
    }
    
    @Bean
    public RmiServiceExporter storageExporter() {
    	RmiServiceExporter rmiServiceExporter = new RmiServiceExporter();
    	rmiServiceExporter.setService(crazyShop());
    	rmiServiceExporter.setServiceName("CrazyShop");
    	rmiServiceExporter.setServiceInterface(CrazyShop.class);
    	rmiServiceExporter.setRegistryPort(CRAZY.RMI_PORT_CrazyShop);
    	return rmiServiceExporter;
    }

    @Bean
    public Harvester harvester() {
    	FileHarvester harvester = new FileHarvester();
    	harvester.setVideoStoragePaths(videoStoragePaths);
    	harvester.setImageStoragePaths(imageStoragePaths);
    	return harvester;
    }
    
}
