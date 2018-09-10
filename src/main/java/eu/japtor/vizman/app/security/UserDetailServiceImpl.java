package eu.japtor.vizman.app.security;

import eu.japtor.vizman.backend.entity.Role;
import eu.japtor.vizman.backend.entity.Usr;
import eu.japtor.vizman.backend.service.UsrService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Service
@Primary
public class UserDetailServiceImpl  implements UserDetailsService {

    @Autowired
    UsrService usrService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        Usr usr = usrService.getUsrByUsername(username);

//            return User.withUsername(usr.getUsername())
//                    .password(usr.getPassword())
////                    .roles("ROLE")
//                    .authorities(getGrantedAuthorities())
//                    .build();

        return new User(
                usr.getUsername()
                , usr.getPassword()
                , true
                , true
                , true
                , true
                , getGrantedAuthorities(usr.getRoles())
        );

//        if(username.equals("test")) {
//            return User.withDefaultPasswordEncoder()
//                    .username("test")
//                    .password("test")
//                    .roles("test")
//                    .build();
//        } else {
//            return null;
//        }
    }

//    private List<GrantedAuthority> getGrantedAuthorities(List<String> privileges) {
    private List<GrantedAuthority> getGrantedAuthorities(Collection<Role> roles) {

        List<GrantedAuthority> authorities = new ArrayList<>();
//        for (String privilege : privileges) {
//            authorities.add(new SimpleGrantedAuthority(privilege));
//        }

        for (Role role : roles) {
            authorities.add(new SimpleGrantedAuthority(role.getName()));
        }

//        authorities.add(new SimpleGrantedAuthority("ADMIN"));
//        authorities.add(new SimpleGrantedAuthority("USER"));
        return authorities;
    }

//    private Collection<? extends GrantedAuthority> getAuthorities(Collection<Role> roles) {
//
//        List<GrantedAuthority> authorities = new ArrayList<>();
//        for (Role role: roles) {
//            authorities.add(new SimpleGrantedAuthority(role.getName()));
//            role.getPrivileges().stream()
//                    .map(p -> new SimpleGrantedAuthority(p.name()))
//                    .forEach(authorities::add);
//        }
//
//        return authorities;
//    }
}
