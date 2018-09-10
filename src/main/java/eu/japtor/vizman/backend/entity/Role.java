package eu.japtor.vizman.backend.entity;

import javax.persistence.*;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@Entity
public class Role extends AbstractEntity {

    public String name;
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String description;
    public String getDesription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }

    @ManyToMany(mappedBy = "roles")
    private Set<Usr> usrs;

//    @ManyToMany(fetch = FetchType.EAGER)
//    @JoinTable(
//            name = "role_privilege",
//            joinColumns = @JoinColumn(
//                    name = "role_id", referencedColumnName = "id"),
//            inverseJoinColumns = @JoinColumn(
//                    name = "privilege_id", referencedColumnName = "id"))
//    @Enumerated(EnumType.STRING)

    @ElementCollection(targetClass=Privilege.class)
    @Enumerated(EnumType.STRING) // Possibly optional (I'm not sure) but defaults to ORDINAL.
    @CollectionTable(name="role_privilege")
    @Column(name="privilege") // Column name in role_privilege
    protected Set<Privilege> privileges = new HashSet<>();
    public Set<Privilege> getPrivileges() {
        return privileges;
    }
    public void setPrivileges(Set<Privilege> privileges) {
        this.privileges = privileges;
    }
}
