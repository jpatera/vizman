package eu.japtor.vizman;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.web.servlet.error.ErrorMvcAutoConfiguration;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

/**
 * The entry point of the Spring Boot application.
 */

//@EnableScheduling

@SpringBootApplication(exclude = ErrorMvcAutoConfiguration.class)
// @Theme(value = "vizman2")
//xx @Viewport("width=device-width, minimum-scale=1.0, initial-scale=1.0, user-scalable=yes")    // ###***
//@Push(PushMode.MANUAL)
//xx @Push   // TODO: Try to move push to DochView?  https://vaadin.com/docs/v13/flow/advanced/tutorial-push-access.html

//public class Application extends SpringBootServletInitializer implements AppShellConfigurator {
public class Application extends SpringBootServletInitializer {

// TODO: Preparation fro Vaadin 19+
//    @Override
//    public void configurePage(AppShellSettings settings) {
////        settings.setViewport("width=device-width, initial-scale=1");
//        // Setters have priority over annotations
//        settings.setPageTitle("A cool vaadin app");
//    }
//    @Override
//    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
//        return application.sources(Application.class);
//    }

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
//        LaunchUtil.launchBrowserInDevelopmentMode(SpringApplication.run(Application.class, args));
    }

}
