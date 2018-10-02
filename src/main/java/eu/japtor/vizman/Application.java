package eu.japtor.vizman;

import com.vaadin.flow.component.html.H3;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import static eu.japtor.vizman.ui.util.VizmanConst.TITLE_ZAK;


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
