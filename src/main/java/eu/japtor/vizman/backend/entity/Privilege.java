package eu.japtor.vizman.backend.entity;

import org.springframework.security.core.GrantedAuthority;

import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import java.util.Collection;
import java.util.Set;

//@Entity
//public class Privilege extends AbstractEntity{
public enum Privilege implements GrantedAuthority {

    VIEW_ALL("Může otevřít všechny definované stránky"),
    MANAGE_ALL("Může vytvářet/editovat/rušit všechna dostupná data"),
    ZAK_VIEW_BASIC_READ("Může otevřít stránku zakázek, přístup pouze k základním údajům"),
    ZAK_VIEW_BASIC_MANAGE("Může vytvářet/editovat/rušit zakazky, přístup pouze k základním údajům"),
    ZAK_VIEW_EXT_READ("Může otevřít stránku zakázek, přístup ke všem údajům"),
    ZAK_VIEW_EXT_MANAGE("Může vytvářet/editovat/rušit zakazky, přístup ke všem údajům"),
    USR_VIEW_BASIC_READ("Může otevřít stránku uživatelů, přístup pouze k základním údajům"),
    USR_VIEW_EXT_READ("Může otevřít stránku uživatelů, přístup ke všem údajům"),
    CONFIG_VIEW_MANAGE("Může otevřít konfigurační stránku VizMana");

    Privilege(String description) {
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

//    public String name;
//    public String getName() {
//        return name;
//    }
//    public void setName(String name) {
//        this.name = name;
//    }

//    @ManyToMany(mappedBy = "privileges")
//    private Set<Role> roles;
//    public Set<Role> getRoles() {
//        return roles;
//    }
//    public void setRoles(Set<Role> roles) {
//        this.roles = roles;
//    }
}
