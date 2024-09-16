package indeed.googledrive_derisking;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "indeed.googledrive_derisking")
@ConfigurationPropertiesScan(basePackages = "indeed.googledrive_derisking")
public class GoogledriveDeriskingApplication {
    public static void main(String[] args) {
        SpringApplication.run(GoogledriveDeriskingApplication.class, args);
    }
}
