package eu.japtor.vizman.backend.entity;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Represents a Person
 */
@Entity
@Table(name = "PERSON")
public class Person extends AbstractGenIdEntity implements HasItemType {

    public static final GrammarGender GENDER = GrammarGender.MASCULINE;
//    public static final String NOMINATIVE_SINGULAR = "Uživatel";
//    public static final String NOMINATIVE_PLURAL = "Uživatelé";
//    public static final String GENITIVE_SINGULAR = "Uživatele";
//    public static final String GENITIVE_PLURAL = "Uživatelů";
//    public static final String ACCUSATIVE_SINGULAR = "Uživatele";
//    public static final String ACCUSATIVE_PLURAL = "Uživatele";

    @Override
    @Transient
    public ItemType getTyp() {
        return ItemType.PERSON;
    }

    @Column(name="STATE")
    @Enumerated(EnumType.STRING)
    private PersonState state = PersonState.LOGGEDOUT;

    @Column(name="HIDDEN")
    private Boolean hidden = false;

    @Column(name="LOGIN_ENABLED")
    private Boolean loginEnabled = false;

    @Column(name="USERNAME")
    private String username = "";

    @Column(name="PASSWORD")
    private String password = "";

    @Column(name="JMENO")
    private String jmeno = "";

    @Column(name="PRIJMENI")
    private String prijmeni = "";

    @Column(name="NASTUP")
    private LocalDate nastup;

    @Column(name="VYSTUP")
    private LocalDate vystup;

    @Column(name="SAZBA")
    private BigDecimal sazba = BigDecimal.ZERO;

    @OneToMany(mappedBy = "person",
            fetch = FetchType.EAGER, cascade = CascadeType.REFRESH, orphanRemoval = false)
    @OrderBy("ymFrom DESC")
    private List<PersonWage> wages = new ArrayList<>();

    @ManyToMany(
            fetch = FetchType.EAGER,
            cascade = {
                    CascadeType.PERSIST,
                    CascadeType.MERGE
            }
    )
    @JoinTable(
            name = "person_role",
            joinColumns = @JoinColumn(
                    name = "person_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(
                    name = "role_id", referencedColumnName = "id"))
    private Set<Role> roles;

    @Transient
    public BigDecimal getWageCurrent() {
        return wages.stream()
                .filter(wage -> wage.getYmTo() == null)
                .map(wage -> null == wage.getTariff() ? BigDecimal.ZERO : wage.getTariff())
                .findFirst().orElse(BigDecimal.ZERO)
        ;
    }

    @Transient
    public YearMonth getYmFromCurrent() {
        return
                wages.stream()
                        .filter(wage -> wage.getYmTo() == null)
                        .map(wage -> null == wage.getYmFrom() ? null : wage.getYmFrom())
                        .findFirst().orElse(null)
                ;
    }

// -----------------------------------------------------

    public Person() {
        roles = new HashSet<>();
    }

    public PersonState getState() {
        return state;
    }
    public void setState(PersonState state) {
        this.state = state;
    }

    public Boolean getHidden() {
        return hidden;
    }
    public void setHidden(Boolean hidden) {
        this.hidden = hidden;
    }

    public Boolean getLoginEnabled() {
        return loginEnabled;
    }
    public void setLoginEnabled(Boolean loginEnabled) {
        this.loginEnabled = loginEnabled;
    }

    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }

    public String getJmeno() {
        return jmeno;
    }
    public void setJmeno(String jmeno) {
        this.jmeno = jmeno;
    }

    public String getPrijmeni() {
        return prijmeni;
    }
    public void setPrijmeni(String prijmeni) {
        this.prijmeni = prijmeni;
    }

    public LocalDate getNastup() {
        return nastup;
    }
    public void setNastup(LocalDate nastup) {
        this.nastup = nastup;
    }

    public LocalDate getVystup() {
        return vystup;
    }
    public void setVystup(LocalDate vystup) {
        this.vystup = vystup;
    }

//    public BigDecimal getSazba() {
//        return sazba;
//    }
//    public void setSazba(BigDecimal sazba) {
//        this.sazba = sazba;
//    }

    public List<PersonWage> getWages() {
        return wages;
    }
    public void setWages(List<PersonWage> wages) {
        this.wages = wages;
    }

    public Set<Role> getRoles() {
        return roles;
    }
    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }


    @Override
    public String toString() {
        // Must use getters instead of direct member access,
        // to make it work with proxy objects generated by the view model
        return "Person{" + getId()  + " : " + getUsername() + " : " + getJmeno() + " " + getPrijmeni() + '}';
    }
}
