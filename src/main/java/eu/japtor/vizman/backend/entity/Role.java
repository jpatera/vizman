package eu.japtor.vizman.backend.entity;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "ROLE")
public class Role extends AbstractEntity {

    public String name;
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String description;
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }

    @ManyToMany(mappedBy = "roles")
    private Set<Person> persons;

    @ElementCollection(targetClass=Perm.class, fetch = FetchType.EAGER)
    @Enumerated(EnumType.STRING) // Possibly optional (I'm not sure) but defaults to ORDINAL.
    @CollectionTable(name="role_perm")
    @Column(name="perm", nullable=false) // Column name in role_perm
    private Set<Perm> perms = new HashSet<>();
    public Set<Perm> getPerms() {
        return perms;
    }
    public void setPerms(Set<Perm> perms) {
        this.perms = perms;
    }
}
