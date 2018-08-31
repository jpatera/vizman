package eu.japtor.vizman.app.security;

import eu.japtor.vizman.backend.entity.Usr;
import eu.japtor.vizman.backend.service.UsrService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@Primary
public class UserDetailServiceImpl  implements UserDetailsService {

    @Autowired
    UsrService usrService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Usr usr = usrService.getUsrByUsername(username);

            return User.withUsername(usr.getUsername())
                    .password(usr.getPassword())
                    .roles("ROLE")
                    .build();

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
}
