package qa.dboyko.rococo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import qa.dboyko.rococo.service.PropertiesLogger;

@SpringBootApplication
public class RococoArtistApplication {

  public static void main(String[] args) {
    SpringApplication springApplication = new SpringApplication(RococoArtistApplication.class);
    springApplication.addListeners(new PropertiesLogger());
    springApplication.run(args);
  }

}
