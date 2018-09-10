package eu.japtor.vizman.app.security;

import eu.japtor.vizman.backend.entity.Privilege;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.EnumSet;
import java.util.Set;

//@Component
// FIXME: Not found yet how and where to inject this mapper
public class AuthoritiesMapper implements GrantedAuthoritiesMapper {

    @Override
    public Collection<? extends GrantedAuthority> mapAuthorities(Collection<? extends GrantedAuthority> authorities) {
        Set<Privilege> permissions = EnumSet.noneOf(Privilege.class);

        for (GrantedAuthority a: authorities) {
            permissions.add((Privilege)a);
//            if ("MY ADMIN GROUP".equals(a.getAuthority())) {
//                permissions.add(Privilege.VIEW_ALL);
//            } else if ("MY USER GROUP".equals(a.getAuthority())) {
//                permissions.add(Privilege.ROLE_USER);
//            }
        }

        return permissions;
    }
}
