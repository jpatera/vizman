package eu.japtor.vizman.backend.entity;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Represents a relation table PERSON_ROLE
 */
@Entity @IdClass(PersonRole.PersonRoleId.class)
@Table(name = "PERSON_ROLE")
public class PersonRole{

    @Id
    @Column(name="PERSON_ID")
    private Long personId;

    @Id
    @Column(name="ROLE_ID")
    private Long roleId;


// -----------------------------------------------------

    public Long getPersonId() {
        return personId;
    }
    public void setPersonId(Long personId) {
        this.personId = personId;
    }

    public Long getRoleId() {
        return roleId;
    }
    public void setRoleId(Long roleId) {
        this.roleId = roleId;
    }

    public class PersonRoleId implements Serializable {
        Long personId;
        Long roleId;
    }

}

