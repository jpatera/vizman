package eu.japtor.vizman.backend.entity;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Immutable;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Predicate;

@Immutable
//@ReadOnly
@Entity
@Table(name = "ZAK_BASIC_VIEW")
public class ZakBasic implements Serializable, HasItemType, HasArch {
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "ID", updatable = false, nullable = false)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Basic
    @Column(name = "TYP")
    private ItemType typ;

    @Basic
    @Column(name = "CKONT")
    private String ckont;

    @Basic
    @Column(name = "CZAK")
    private Integer czak;

    @Basic
    @Column(name = "SKUPINA")
    private String skupina;

    @Basic
    @Column(name = "ROK")
    private Integer rok;

    @Basic
    @Column(name = "TEXT_KONT")
    private String textKont;

    @Basic
    @Column(name = "TEXT_ZAK")
    private String textZak;

    @Basic
    @Column(name = "OBJEDNATEL")
    private String objednatel;

    @Basic
    @Column(name = "ARCH")
    private Boolean arch;

    @Basic
    @Column(name = "ID_KONT")
    private Long idKont;

    @Transient
    private boolean checked;



    public Long getId() {
        return id;
    }

    public ItemType getTyp() {
        return typ;
    }

    public String getCkont() {
        return ckont;
    }

    public Integer getCzak() {
        return czak;
    }

    public Integer getRok() {
        return rok;
    }

    public String getSkupina() {
        return skupina;
    }

    public String getTextKont() {
        return textKont;
    }

    public String getTextZak() {
        return textZak;
    }

    public String getObjednatel() {
        return objednatel;
    }

    public Boolean getArch() {
        return arch;
    }

    public Long getIdKont() {
        return idKont;
    }

    @Transient
    public boolean isChecked() {
        return checked;
    }


//    public void setId(Long id) {
//        this.id = id;
//    }
//
//    public void setTyp(ItemType typ) {
//        this.typ = typ;
//    }
//
//    public void setCkont(String ckont) {
//        this.ckont = ckont;
//    }
//
//    public void setCzak(Integer czak) {
//        this.czak = czak;
//    }
//
//    public void setSkupina(String skupina) {
//        this.skupina = skupina;
//    }
//
//    public void setRok(Integer rok) {
//        this.rok = rok;
//    }
//
//    public void setTextKont(String textKont) {
//        this.textKont = textKont;
//    }
//
//    public void setTextZak(String textZak) {
//        this.textZak = textZak;
//    }
//
//    public void setObjednatel(String objednatel) {
//        this.objednatel = objednatel;
//    }
//
//    public void setArch(Boolean arch) {
//        this.arch = arch;
//    }
//
//    public void setIdKont(Long idKont) {
//        this.idKont = idKont;
//    }

    @Transient
    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    @Transient
    public String getCkontNotNull() {
        return null == ckont ? "" : ckont;
    }

    @Transient
    public String getCzakNotNull() {
        return null == czak ? "" : czak.toString();
    }

    @Transient
    public String getTextKontNotNull() {
        return null == textKont ? "" : textKont;
    }

    @Transient
    public String getTextZakNotNull() {
        return null == textZak ? "" : textZak.toString();
    }

    @Transient
    public String getKzCislo() {
        StringBuilder builder = new StringBuilder();
        builder .append(getCkontNotNull())
                .append(" / ")
                .append(getCzakNotNull())
        ;
        return builder.toString();
    }

    @Transient
    public String getKzText() {
        StringBuilder builder = new StringBuilder();
        builder .append(StringUtils.substring(getTextKontNotNull(), 0, 25))
                .append(" / ")
                .append(getTextZakNotNull())
        ;
        return builder.toString();
    }

//    @Transient
//    @Override
//    public boolean isChecked() {
//        return checked;
//    }

//    @Override
//    public void setChecked(boolean checked) {
//        this.checked = checked;
//    }


    @Override
    public int hashCode() {
        return 31;
//		if (id == null) {
//			return super.hashCode();
//		}
//		return 31 + id.hashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (!(other instanceof AbstractGenIdEntity)) return false;
        return id != null && id.equals(((ZakBasic) other).id);
//		if (id == null) {
//			// New entities are only equal if the instance is the same
//			return super.equals(other);
//		}
    }
}
