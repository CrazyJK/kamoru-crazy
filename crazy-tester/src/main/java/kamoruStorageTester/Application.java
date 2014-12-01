package kamoruStorageTester;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ClassPathXmlApplicationContext;

@Configuration
@ComponentScan
@EnableAutoConfiguration
public class Application {

	@SuppressWarnings("resource")
    public static void main(String[] args) {

		ApplicationContext context = new ClassPathXmlApplicationContext("tester-conf.xml");
    	   
           ServiceTester tester = context.getBean(ServiceTester.class);        
           tester.getVideo();
           tester.getVideoList();
    }
    
}
