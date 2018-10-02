package eu.japtor.vizman.app.security;

import eu.japtor.vizman.backend.entity.Perm;
import eu.japtor.vizman.backend.entity.Role;
import eu.japtor.vizman.backend.entity.Person;
import eu.japtor.vizman.backend.service.PersonService;
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
    PersonService personService;

    @Autowired
    public UserDetailServiceImpl(PersonService personService) {
//        Assert.nonNull("");
//        Objects.nonNull(personService);
        this.personService = personService;
    }

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {


//        Optional<UserObject> user = users.stream()
//                .filter(u -> u.name.equals(username))
//                .findAny();
//        if (!user.isPresent()) {
//            throw new UsernameNotFoundException("User not found by name: " + username);
//        }
//        return toUserDetails(user.get());


        Person person = personService.getByUsername(username);

//            return User.withUsername(person.getUsername())
//                    .password(person.getPassword())
////                    .roles("ROLE")
//                    .authorities(getGrantedAuthorities())
//                    .build();

        return new User(
                person.getUsername()
                , person.getPassword()
                , true
                , true
                , true
                , true
                , getGrantedAuthorities(person.getRoles())
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
