package eu.japtor.vizman.app.security;

import eu.japtor.vizman.backend.entity.Perm;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;

import java.util.Collection;
import java.util.EnumSet;
import java.util.Set;

//@Component
// FIXME: Not found yet how and where to inject this mapper
public class AuthoritiesMapper implements GrantedAuthoritiesMapper {

    @Override
    public Collection<? extends GrantedAuthority> mapAuthorities(Collection<? extends GrantedAuthority> authorities) {
        Set<Perm> perms = EnumSet.noneOf(Perm.class);

        for (GrantedAuthority a: authorities) {
            perms.add((Perm)a);
//            if ("MY ADMIN GROUP".equals(a.getAuthority())) {
//                permissions.add(Perm.VIEW_ALL);
//            } else if ("MY USER GROUP".equals(a.getAuthority())) {
//                permissions.add(Perm.ROLE_USER);
//            }
        }

        return perms;
    }
}
