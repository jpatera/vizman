package eu.japtor.vizman.backend.entity;

import eu.japtor.vizman.ui.components.VzIconBox;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;


@Entity
@Table(name = "NAB_VIEW")
public class Nab extends AbstractGenIdEntity implements HasItemType, HasVzState, HasModifDates {

    @Column(columnDefinition = "BINARY(16)")
    private UUID uuid;

    @Enumerated(EnumType.STRING)
    @Basic
    @Column(name = "TYP")
    private ItemType typ;

    @Basic
    @Column(name = "ROK")
    private Integer rok;

    @Basic
    @Column(name = "CNAB")
    private String cnab;

    @Basic
    @Column(name = "VZ")
    private Boolean vz;

    @Basic
    @Column(name = "TEXT")
    private String text;

    @Basic
    @Column(name = "FOLDER")
    private String folder;

    @Basic
    @Column(name = "DATE_CREATE")
    @CreationTimestamp
    private LocalDate dateCreate;

    @Basic
    @Column(name = "DATETIME_UPDATE")
    private LocalDateTime datetimeUpdate;

    @Basic
    @Column(name = "POZNAMKA")
    private String poznamka;

    @Basic
    @Column(name = "OBJEDNATEL")
    private String objednatel;

    @Basic
    @Column(name = "CKONT")
    private String ckont;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_KONT")
    private Kont kont;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_KLIENT")
    private Klient klient;


    public ItemType getTyp() {
        return typ;
    }
    public void setTyp(ItemType typ) {
        this.typ = typ;
    }

    public Integer getRok() {
        return rok;
    }
    public void setRok(Integer rok) {
        this.rok = rok;
    }

    public String getCnab() {
        return cnab;
    }
    public void setCnab(String text) {
        this.cnab = cnab;
    }

    public String getText() {
        return text;
    }
    public void setText(String text) {
        this.text = text;
    }

    public String getFolder() {
        return folder;
    }
    public void setFolder(String docdir) {
        this.folder = docdir;
    }

    public Boolean getVz() {
        return vz;
    }
    public void setVz(Boolean vz) {
        this.vz = vz;
    }

    public LocalDate getDateCreate() {
        return dateCreate;
    }
    public void setDateCreate(LocalDate dateCreate) {
        this.dateCreate = dateCreate;
    }

    public LocalDateTime getDatetimeUpdate() {
        return datetimeUpdate;
    }
    public void setDatetimeUpdate(LocalDateTime datetimeUpdate) {
        this.datetimeUpdate = datetimeUpdate;
    }

    public String getPoznamka() {
        return poznamka;
    }
//    public void setPoznamka(String poznamka) {
//        this.poznamka = poznamka;
//    }

    public String getObjednatel() {
        return objednatel;
    };
//    public void setObjednatel(String objednatel) {
//        this.objednatel = objednatel;
//    }

    public String getCkont() {
        return ckont;
    };
//    public void setCkont(String ckont) {
//        this.ckont = ckont;
//    }



    // Transient fields
    // ================

    @Override
    public VzIconBox.VzState getVzState() {
        return (null == vz) || !vz ? VzIconBox.VzState.PUBLIC : VzIconBox.VzState.NOTPUBLIC;
    }

//    @Transient
//    private Predicate<Fakt> afterTermsPredicate = fakt ->
//        ((null == fakt.getDateVystav()) && (null != fakt.getDateDuzp()) && (fakt.getDateDuzp().plusDays(1).isBefore(LocalDate.now())))
//    ;
//
//    @Transient
//    private Predicate<Fakt> beforeTermsPredicate = fakt ->
//        ((null == fakt.getDateVystav()) && (null != fakt.getDateDuzp()) && (fakt.getDateDuzp().isAfter(LocalDate.now())))
//    ;

    // ========================================

    public Nab() {
        super();
    }

    public Nab(ItemType typ) {
        super();
        this.typ = typ;
        this.rok = LocalDate.now().getYear();
        this.uuid = UUID.randomUUID();
        this.vz = false;
    }

    // ========================================

    @Override
    public int hashCode() {
        return uuid.hashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (!(other instanceof AbstractGenIdEntity)) return false;
        return getId() != null && getId().equals(((AbstractGenIdEntity) other).getId());
    }
}
