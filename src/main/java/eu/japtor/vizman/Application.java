package eu.japtor.vizman;

import eu.japtor.vizman.app.security.SecurityConfiguration;
import eu.japtor.vizman.ui.MainView;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * The entry point of the Spring Boot application.
 */


//@SpringBootApplication(scanBasePackageClasses = {
//        SecurityConfiguration.class, MainView.class, Application.class})
////        UserService.class }, exclude = ErrorMvcAutoConfiguration.class)

//@EnableJpaRepositories(basePackageClasses = { UserRepository.class })
//@EntityScan(basePackageClasses = { User.class })

@SpringBootApplication
public class Application {
//public class Application extends SpringBootServletInitializer {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

//    @Override
//    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
//        return application.sources(Application.class);
//    }
}
