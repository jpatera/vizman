package eu.japtor.vizman.backend.entity;

import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public enum Perm implements GrantedAuthority {

    VIEW_ALL("Může otevřít všechny definované stránky"),
    MANAGE_ALL("Může vytvářet/editovat/rušit všechna dostupná data"),

    KONT_VIEW_BASIC_READ("Může otevřít stránku kontraktů, přístup pouze k základním údajům"),
    KONT_VIEW_BASIC_MANAGE("Může vytvářet/editovat/rušit kontrakty, přístup pouze k základním údajům"),
    KONT_VIEW_EXT_READ("Může otevřít stránku kontraktů, přístup ke všem údajům"),
    KONT_VIEW_EXT_MANAGE("Může vytvářet/editovat/rušit kontrakty, přístup ke všem údajům"),

    ZAK_VIEW_BASIC_READ("Může otevřít stránku zakázek, přístup pouze k základním údajům"),
    ZAK_VIEW_BASIC_MANAGE("Může vytvářet/editovat/rušit zakazky, přístup pouze k základním údajům"),
    ZAK_VIEW_EXT_READ("Může otevřít stránku zakázek, přístup ke všem údajům"),
    ZAK_VIEW_EXT_MANAGE("Může vytvářet/editovat/rušit zakazky, přístup ke všem údajům"),

    USR_VIEW_BASIC_READ("Může otevřít stránku uživatelů, přístup pouze k základním údajům"),
    USR_VIEW_EXT_READ("Může otevřít stránku uživatelů, přístup ke všem údajům"),
    CONFIG_VIEW_MANAGE("Může otevřít konfigurační stránku VizMana");

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
