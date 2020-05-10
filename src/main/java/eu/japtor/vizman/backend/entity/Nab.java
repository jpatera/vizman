package eu.japtor.vizman.backend.entity;

import eu.japtor.vizman.backend.service.NabService;
import eu.japtor.vizman.ui.components.VzIconBox;
import org.apache.commons.lang3.StringUtils;
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


// -----------------------------------------------------


    public static Nab getEmptyInstance() {
        Nab n = new Nab();
        n.setRok(null);
        n.setCnab(null);
        n.setCkont(null);
        n.setVz(null);
        n.setText(null);
        n.setObjednatel(null);
        n.setPoznamka(null);
        return n;
    }

    public static Nab getInstanceFromFilter(NabService.NabFilter nabFilter) {
        if (null == nabFilter) {
            return Nab.getEmptyInstance();
        } else {
            Nab n = Nab.getEmptyInstance();
                n.setRok(nabFilter.getRok());
                n.setCnab(nullIfBlank(nabFilter.getCnab()));
                n.setCkont(nullIfBlank(nabFilter.getCkont()));
                n.setVz(nabFilter.getVz());
                n.setText(nullIfBlank(nabFilter.getText()));
                n.setObjednatel(nullIfBlank(nabFilter.getObjednatel()));
                n.setPoznamka(nullIfBlank(nabFilter.getPoznamka()));
            return n;
        }
    }

    private static String nullIfBlank(String str) {
        if (StringUtils.isBlank(str)) {
            return null;
        }
        return str;
    }

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
    public void setCnab(String cnab) {
        this.cnab = cnab;
    }

    public String getCkont() {
        return ckont;
    }
    public void setCkont(String ckont) {
        this.ckont = ckont;
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

    public String getPoznamka() {
        return poznamka;
    }
    public void setPoznamka(String poznamka) {
        this.poznamka = poznamka;
    }

    public String getObjednatel() {
        return objednatel;
    };
    public void setObjednatel(String objednatel) {
        this.objednatel = objednatel;
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
