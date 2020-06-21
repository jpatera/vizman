package eu.japtor.vizman.backend.entity;

import eu.japtor.vizman.backend.service.RoleService;

import javax.persistence.*;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "ROLE")
public class Role extends AbstractGenIdEntity implements HasItemType {

    public String name;
    public String description;

    @Transient
    private ItemType typ = ItemType.ROLE;

    @ElementCollection(targetClass=Perm.class, fetch = FetchType.EAGER)
    @Enumerated(EnumType.STRING) // Possibly optional (I'm not sure) but defaults to ORDINAL.
    @CollectionTable(name="role_perm")
    @Column(name="perm", nullable=false) // Column name in role_perm
    @OrderBy("perm")
    private Set<Perm> perms = new HashSet<>();


    @ManyToMany(
            fetch = FetchType.LAZY,
            cascade = {
                    CascadeType.PERSIST,
                    CascadeType.MERGE
            }
    )
    @JoinTable(
            name = "person_role",
            joinColumns = @JoinColumn(
                    name = "role_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(
                    name = "person_id", referencedColumnName = "id"))
    @OrderBy("prijmeni")
    private List<Person> persons;

    public Role() {
        super();
    }

    public Role(ItemType typ) {
        super();
        this.typ = typ;
        this.name = "";
        this.description = "";
        this.perms = new HashSet<>();
    }


    public static Role getEmptyInstance() {
        Role role = new Role();
        role.setName(null);
        role.setDescription(null);
        role.setPerms(new HashSet<>());
        return role;
    }

    public static Role getNewInstance(Role role) {
        if (null == role) {
            return Role.getEmptyInstance();
        } else {
            Role newRole = Role.getEmptyInstance();
            newRole.setName(role.getName());
            newRole.setDescription(role.getDescription());
            if (null == role.getPerms()) {
                newRole.setPerms(new HashSet<>());
            } else  {
                newRole.setPerms(role.getPerms());
            }
            return newRole;
        }
    }

    public static Role getInstanceFromFilter(RoleService.RoleFilter roleFilter) {
        if (null == roleFilter) {
            return Role.getEmptyInstance();
        } else {
            Role role = Role.getEmptyInstance();
            role.setName(roleFilter.getName());
            role.setDescription(roleFilter.getDescription());
            return role;
        }
    }

    @Override
    public ItemType getTyp() {
        return typ;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }

    public Set<Perm> getPerms() {
        return perms;
    }
    public void setPerms(Set<Perm> perms) {
        this.perms = perms;
    }

    public List<Person> getPersons() {
        return persons;
    }
}
