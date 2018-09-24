package eu.japtor.vizman.app.security;

import eu.japtor.vizman.backend.entity.Perm;
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
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


@Service
@Primary
public class UserDetailServiceImpl  implements UserDetailsService {

    final
    UsrService usrService;

    @Autowired
    public UserDetailServiceImpl(UsrService usrService) {
//        Assert.nonNull("");
//        Objects.nonNull(usrService);
        this.usrService = usrService;
    }

    @Override
    @Transactional
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
    }

    private List<GrantedAuthority> getGrantedAuthorities(Collection<Role> roles) {

        List<GrantedAuthority> authorities = new ArrayList<>();
        for (Role role : roles) {
            for (Perm perm : role.getPerms()) {
                authorities.add(new SimpleGrantedAuthority(perm.getAuthority()));
            }
        }
        return authorities;
    }
}
