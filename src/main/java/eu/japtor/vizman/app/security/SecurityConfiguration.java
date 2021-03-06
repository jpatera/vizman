package eu.japtor.vizman.app.security;

// import com.vaadin.flow.server.ServletHelper;
import com.vaadin.flow.server.HandlerHelper;
import com.vaadin.flow.shared.ApplicationConstants;
import eu.japtor.vizman.backend.entity.Perm;
import eu.japtor.vizman.backend.entity.Person;
import eu.japtor.vizman.backend.repository.PersonRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.servlet.http.HttpServletRequest;

import java.util.stream.Stream;

import static eu.japtor.vizman.ui.util.VizmanConst.*;

/**
 * Configures spring security, doing the following:
 * <li>Bypass security checks for static resources,</li>
 * <li>Restrict access to the application, allowing only logged in users,</li>
 * <li>Set up the login form,</li>
 */
@EnableWebSecurity
// @EnableGlobalMethodSecurity
@Configuration
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    private final UserDetailsService userDetailsService;

    @Autowired
    private PasswordEncoder passwordEncoder;

//    /**
//     * The password encoder to use when encrypting passwords.
//     */
//    @SuppressWarnings("deprecation")
//    @Bean
//    public PasswordEncoder passwordEncoder() {
//        return new BCryptPasswordEncoder();
//    }

    @SuppressWarnings("deprecation")
    @Bean
    public PasswordEncoder passwordEncoder() {
        return NoOpPasswordEncoder.getInstance();
    }

    @Autowired
    public SecurityConfiguration(UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }


    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public Person currentUser(PersonRepo userRepo) {
        return userRepo.findTopByUsernameIgnoreCase(SecurityUtils.getUsername());
    }


    @Override
    public void configure(AuthenticationManagerBuilder auth) throws Exception {

        // Registers our UserDetailsService and the password encoder to be used on login attempts.
        super.configure(auth);
        auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder);
    }

    /**
     * Require login to access internal pages and configure login form.
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // Not using Spring CSRF here to be able to use plain HTML for the login page
        http.csrf().disable()

                // Register our CustomRequestCache, that saves unauthorized access attempts, so
                // the user is redirected after login.
                .requestCache().requestCache(new CustomRequestCache())

                // Restrict access to our application.
                .and().authorizeRequests()

                // Allow all flow internal requests.
                // Bez toho to vyhazuje vpravo nahore JSON error
                .requestMatchers(SecurityUtils::isFrameworkInternalRequest).permitAll()

                    .antMatchers("/" + ROUTE_DOCH).hasAnyAuthority(
                            Perm.DOCH_USE.name(),
                            Perm.VIEW_ALL.name(), Perm.MODIFY_ALL.name())

                    .antMatchers("/" + ROUTE_PRUH).hasAnyAuthority(
                            Perm.PRUH_USE.name(),
                            Perm.VIEW_ALL.name(), Perm.MODIFY_ALL.name())

                    .antMatchers("/" + ROUTE_PERSON).hasAnyAuthority(
                            Perm.PERSON_BASIC_READ.name(), Perm.PERSON_EXT_READ.name(),
                            Perm.VIEW_ALL.name(), Perm.MODIFY_ALL.name())

                    .antMatchers("/" + ROUTE_ROLE).hasAnyAuthority(
                            Perm.ROLE_READ.name(),
                            Perm.VIEW_ALL.name(), Perm.MODIFY_ALL.name())

                    .antMatchers("/" + ROUTE_KONT).hasAnyAuthority(
                            Perm.ZAK_BASIC_READ.name(), Perm.ZAK_EXT_READ.name(),
                            Perm.VIEW_ALL.name(), Perm.MODIFY_ALL.name())

                    .antMatchers("/" + ROUTE_ZAK_LIST).hasAnyAuthority(
                            Perm.ZAK_BASIC_READ.name(), Perm.ZAK_EXT_READ.name(),
                            Perm.VIEW_ALL.name(), Perm.MODIFY_ALL.name())

                    .anyRequest().authenticated()

////                  .antMatchers("/welcome").permitAll()
////  				.requestMatchers(SecurityUtils::isFrameworkInternalRequest).permitAll()
//                    .antMatchers("/" + ROUTE_PERSON).hasRole("ADMIN")
//                    .anyRequest().authenticated()
                    .and()
// 				.and().httpBasic()

//                // Configure the login page.
                .formLogin()
                    .loginPage("/login").permitAll()
                    .defaultSuccessUrl("/home", false)
                    .loginProcessingUrl("/login")
                    .successForwardUrl("/doch")
                    .failureUrl("/login?error")
                    .and()

                .logout()
                    .permitAll()
//                    .logoutSuccessUrl("/zak")
//                    .and()
//                .sessionManagement().sessionAuthenticationStrategy(sessionControlAuthenticationStrategy())
        ;
    }


    /**
     * Allows access to static resources, bypassing Spring security.
     */
    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers(
                // Vaadin Flow static resources
                "/VAADIN/**",

                // the standard favicon URI
                "/favicon.ico",

                // the robots exclusion standard
                "/robots.txt",

                // web application manifest
                "/manifest.webmanifest",
                "/manifest.json",
                "/sw.js",
                "/offline-page.html",

                // icons and images
                "/icons/**",
                "/images/**",

                // (development mode) static resources
                "/frontend/**",

                // (development mode) webjars
                "/webjars/**",

                // (development mode) H2 debugging console
                "/h2-console/**",

                // (production mode) static resources
                "/frontend-es5/**", "/frontend-es6/**");
    }


    static boolean isFrameworkInternalRequest(HttpServletRequest request) {
        final String parameterValue = request.getParameter(ApplicationConstants.REQUEST_TYPE_PARAMETER);
        return parameterValue != null
                && Stream.of(HandlerHelper.RequestType.values()).anyMatch(r -> r.getIdentifier().equals(parameterValue));
    }
}
