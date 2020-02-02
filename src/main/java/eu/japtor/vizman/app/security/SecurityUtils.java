package eu.japtor.vizman.app.security;

import com.vaadin.flow.server.ServletHelper;
import com.vaadin.flow.shared.ApplicationConstants;
import eu.japtor.vizman.backend.entity.Perm;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Stream;

public class SecurityUtils {

    private SecurityUtils() {
        // Util methods only
    }

    /**
     * Gets the user name of the currently signed in user.
     *
     * @return the user name of the current user or <code>null</code> if the user
     *         has not signed in
     */
    public static String getUsername() {
        SecurityContext context = SecurityContextHolder.getContext();
        UserDetails userDetails = (UserDetails) context.getAuthentication().getPrincipal();
        return userDetails.getUsername();
    }

    /**
     * Checks if access is granted for the current user for the given secured view,
     * defined by the view class.
     *
     * @param securedClass
     * @return true if access is granted, false otherwise.
     */
    public static boolean isAccessGranted(Class<?> securedClass) {
        Permissions permissions = AnnotationUtils.findAnnotation(securedClass, Permissions.class);
        if (permissions == null) {
            return true;
        }

        Authentication userAuthentication = SecurityContextHolder.getContext().getAuthentication();
        if (userAuthentication == null) {
            return false;
        }
        Set<String> allowedPermNames = Perm.getPermNames(Arrays.asList(permissions.value()));
        return userAuthentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(allowedPermNames::contains);
//                .anyMatch(a -> allowedAuths.contains(a));
    }

    /**
     * Checks if access is granted for the current user for the price/money related fields,
     *
     * @return true if access  prices/money information is granted, false otherwise.
     */
    public static boolean isHonorareAccessGranted() {
        Authentication userAuthentication = SecurityContextHolder.getContext().getAuthentication();
        if (userAuthentication == null) {
            return false;
        }
        Set<String> moneyPermNames =  Perm.getPermNames(Arrays.asList(Perm.VIEW_ALL, Perm.MODIFY_ALL));
        return userAuthentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(moneyPermNames::contains);
    }

    public static boolean isWagesAccessGranted() {
        Authentication userAuthentication = SecurityContextHolder.getContext().getAuthentication();
        if (userAuthentication == null) {
            return false;
        }
        Set<String> moneyPermNames =  Perm.getPermNames(Arrays.asList(
                Perm.VIEW_ALL, Perm.MODIFY_ALL, Perm.PERSON_EXT_READ)
        );
        return userAuthentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(moneyPermNames::contains);
    }

    public static boolean isZakFormsAccessGranted() {
        Authentication userAuthentication = SecurityContextHolder.getContext().getAuthentication();
        if (userAuthentication == null) {
            return false;
        }
        Set<String> kontZakformPermNames =  Perm.getPermNames(Arrays.asList
                (Perm.VIEW_ALL, Perm.MODIFY_ALL, Perm.ZAK_EXT_READ)
        );
        return userAuthentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(kontZakformPermNames::contains);
    }

    /**
     * Checks if user is allowed select pruh of other userss.
     *
     * @return true if access  prices/money information is granted, false otherwise.
     */
    public static boolean canViewOtherUsers() {
        Authentication userAuthentication = SecurityContextHolder.getContext().getAuthentication();
        if (userAuthentication == null) {
            return false;
        }
        Set<String> otherUsersPermNames =  Perm.getPermNames(Arrays.asList(
                Perm.VIEW_OTHER_USERS, Perm.VIEW_ALL, Perm.MODIFY_ALL));
        return userAuthentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(otherUsersPermNames::contains);
//                .anyMatch(a -> allowedAuths.contains(a));
    }


    /**
     * Checks if the user is logged in.
     *
     * @return true if the user is logged in. False otherwise.
     */
    public static boolean isUserLoggedIn() {
        SecurityContext context = SecurityContextHolder.getContext();
        return context.getAuthentication() != null
                && !(context.getAuthentication() instanceof AnonymousAuthenticationToken);
    }

    /**
     * Tests if the request is an internal framework request. The test consists of
     * checking if the request parameter is present and if its value is consistent
     * with any of the request types know.
     *
     * @param request
     *            {@link HttpServletRequest}
     * @return true if is an internal framework request. False otherwise.
     */
    static boolean isFrameworkInternalRequest(HttpServletRequest request) {
        final String parameterValue = request.getParameter(ApplicationConstants.REQUEST_TYPE_PARAMETER);
        return parameterValue != null
                && Stream.of(ServletHelper.RequestType.values()).anyMatch(r -> r.getIdentifier().equals(parameterValue));
    }

}
