package eu.japtor.vizman.backend.entity;

import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public enum Perm implements GrantedAuthority {

    VIEW_ALL("Lze prohlížet všechna dostupná data"),
    MANAGE_ALL("Lze vytvářet/editovat/rušit všechna dostupná data"),

    DOCH_USE("Lze zanamenávat docházku"),

    KONT_VIEW_BASIC_READ("Lze prohlížet seznam kontraktů, přístup pouze k základním údajům"),
    KONT_VIEW_BASIC_MANAGE("Lze vytvářet/editovat/rušit kontrakty, přístup pouze k základním údajům"),
    KONT_VIEW_EXT_READ("Lze prohlížet seznam kontraktů, přístup ke všem údajům"),
    KONT_VIEW_EXT_MANAGE("Lze vytvářet/editovat/rušit kontraktz, přístup ke všem údajům"),

    ZAK_VIEW_BASIC_READ("Lze prohlížet seznam zakázek, přístup pouze k základním údajům"),
    ZAK_VIEW_BASIC_MANAGE("Lze vytvářet/editovat/rušit zakazky, přístup pouze k základním údajům"),
    ZAK_VIEW_EXT_READ("Lze prohlížet seznam zakázek, přístup ke všem údajům"),
    ZAK_VIEW_EXT_MANAGE("Lze vytvářet/editovat/rušit zakazky, přístup ke všem údajům"),

    CONFIG_VIEW_MANAGE("Lze měnit konfiguraci VizMana"),

    PERSON_VIEW_BASIC_READ("Lze prohlížet seznam uživatelů, přístup pouze k základním údajům"),
    PERSON_VIEW_EXT_READ("Lze prohlížet seznam uživatelů, přístup ke všem údajům"),

    ROLE_VIEW_READ("Lze prohlížet seznam rolí"),
    ROLE_VIEW_MANAGE("Lze vytvářet/editovat/rušit role"),

    CIN_VIEW_READ("Lze prohlížet seznam činností"),
    ;

    static private HashSet<String> permNames = new HashSet<>();
    static {
        for (Perm p : Perm.values()) {
            permNames.add(p.name());
        }
    }

    Perm(String description) {
        this.description = description;
    }

    private String description;
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String getAuthority() {
        return this.name();
    }

    public static Set<String> getAllPermNames() {
        return permNames;
    }

    public static Set<String> getPermNames(Collection<Perm> perms) {
        return perms.stream()
                .map(Perm::name)
                .collect(Collectors.toSet());
    }
}
