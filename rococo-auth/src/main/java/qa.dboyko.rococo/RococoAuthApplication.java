package qa.dboyko.rococo;

import qa.dboyko.rococo.service.PropertiesLogger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class RococoAuthApplication {

  public static void main(String[] args) {
    SpringApplication springApplication = new SpringApplication(qa.dboyko.rococo.RococoAuthApplication.class);
    springApplication.addListeners(new PropertiesLogger());
    springApplication.run(args);
  }
}
