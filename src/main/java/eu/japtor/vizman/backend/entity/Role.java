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

    @ElementCollection(targetClass=Perm.class)
    @Enumerated(EnumType.STRING) // Possibly optional (I'm not sure) but defaults to ORDINAL.
    @CollectionTable(name="role_perm")
    @Column(name="perm") // Column name in role_perm
    protected Set<Perm> perms = new HashSet<>();
    public Set<Perm> getPerms() {
        return perms;
    }
    public void setPerms(Set<Perm> perms) {
        this.perms = perms;
    }
}
