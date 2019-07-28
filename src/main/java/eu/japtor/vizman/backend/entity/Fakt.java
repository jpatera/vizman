package eu.japtor.vizman.backend.entity;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "FAKT")
public class Fakt extends AbstractGenIdEntity implements HasModifDates, HasItemType {

    @Enumerated(EnumType.STRING)
    @Basic
    @Column(name = "TYP")
    private ItemType typ;

    @Basic
    @Column(name = "CFAKT")
    private Integer cfakt;

    @Column(name = "DATE_DUZP")
    private LocalDate dateDuzp;

    @Column(name = "PLNENI")
    private BigDecimal plneni;

    @Column(name = "TEXT")
    private String text;

    @Column(name = "CASTKA")
    private BigDecimal castka;

    @Column(name = "ZAKLAD")
    private BigDecimal zaklad;

    @Column(name = "DATE_VYSTAV")
    private LocalDate dateVystav;

    @Column(name = "DATETIME_EXPORT")
    private LocalDateTime dateTimeExport;

    @Column(name = "DATE_CREATE", updatable = false)
    @CreationTimestamp
    private LocalDate dateCreate;

    @Column(name = "DATETIME_UPDATE", insertable = false, updatable = false)
    private LocalDateTime datetimeUpdate;

    @Column(name = "FAKT_CISLO")
    private String faktCislo;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "ID_ZAK")
    private Zak zak;


    public Fakt() {

    }

    public Fakt(final ItemType itemType, final Integer cfakt, final Zak zakParent) {
        this.typ = itemType;
        this.cfakt = cfakt;
        this.zak = zakParent;
//        this.plneni = BigDecimal.valueOf(0);
//        this.castka = BigDecimal.valueOf(0);
//        this.zaklad = BigDecimal.valueOf(0);
    }

    public ItemType getTyp() {
        return typ;
    }

    public void setTyp(ItemType typ) {
        this.typ = typ;
    }

    public Integer getCfakt() {
        return cfakt;
    }

    public void setCfakt(Integer cfakt) {
        this.cfakt = cfakt;
    }

    public BigDecimal getPlneni() {
        return plneni;
    }

    public void setPlneni(BigDecimal plneni) {
        this.plneni = plneni;
    }


    public BigDecimal getCastka() {
        return castka;
    }

    public void setCastka(BigDecimal castka) {
        this.castka = castka;
    }

    public BigDecimal getZaklad() {
        return zaklad;
    }

    public void setZaklad(BigDecimal zaklad) {
        this.zaklad = zaklad;
    }


    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }


    public LocalDate getDateDuzp() {
        return dateDuzp;
    }

    public void setDateDuzp(LocalDate dateDuzp) {
        this.dateDuzp = dateDuzp;
    }


    public LocalDate getDateVystav() {
        return dateVystav;
    }

    public void setDateVystav(LocalDate dateVystav) {
        this.dateVystav = dateVystav;
    }

    public String getDateVystavStr() {
        return null == dateVystav ? null : dateVystav.toString();
    }


    public LocalDateTime getDateTimeExport() {
        return dateTimeExport;
    }

    public void setDateTimeExport(LocalDateTime dateTimeExport) {
        this.dateTimeExport = dateTimeExport;
    }

    public String getDateTimeExportStr() {
        return null == dateTimeExport ? null : dateTimeExport.toString();
    }


    public LocalDate getDateCreate() {
        return dateCreate;
    }

    protected void setDateCreate(LocalDate dateCreate) {
        this.dateCreate = dateCreate;
    }


    public LocalDateTime getDatetimeUpdate() {
        return datetimeUpdate;
    }

    protected void setDatetimeUpdate(LocalDateTime datetimeUpdate) {
        this.datetimeUpdate = datetimeUpdate;
    }


    public String getFaktCislo() {
        return faktCislo;
    }

    public void setFaktCislo(String faktCislo) {
        this.faktCislo = faktCislo;
    }

    @Transient
    public Mena getMena() {
        return (null == getZak()) ? null : getZak().getMena();
    }


    public Zak getZak() {
        return zak;
    }

    public void setZak(Zak zak) {
        this.zak = zak;
    }

    @Transient
    public Integer getCzak() {
        return null == zak ? null : zak.getCzak();
    }

    @Transient
    public String getZakEvid() {
        return getCkont() + " / " + getCzak()
                + (null == zak.getText() ? "" : " , " + zak.getText());
    }

    @Transient
    public String getFaktEvid() {
        return getCkont() + " / " + getCzak() + " / " + cfakt;
    }

    @Transient
    public String getFaktExpFileName() {
        return getCkont() + "_#_" + getCzak() + "_#_" + cfakt
                + "_##_" + (null == zak.getKontFolder() ? "NO-KONT-TEXT" : zak.getKontFolder().substring(0, 15))
                + "_#_" + (null == zak.getFolder() ? "NO-ZAK-TEXT" : zak.getFolder().substring(0, 15))
                + ".txt"
        ;
    }

    @Transient
    public BigDecimal getZakHonorar() {
        return null == zak ? BigDecimal.ZERO : zak.getHonorar();
    }

    @Transient
    public String getCkont() {
        return null == zak ? "" : zak.getCkont();
    }

    @Transient
    public boolean isFaktBefore() {
        return ((null == getDateVystav())
                && (null != getDateDuzp())
                && (getDateDuzp().isAfter(LocalDate.now())));
    }

    @Transient
    public boolean isFaktAfter() {
        return ((null == getDateVystav())
                && (null != getDateDuzp())
                && (getDateDuzp().isBefore(LocalDate.now().plusDays(1))));
    }

    @Transient
    public boolean isFakturovano() {
        return null != getCastka() && getCastka().compareTo(BigDecimal.ZERO) > 0;
    }

    @Transient
    public boolean canFakturovat() {
        return (null != getDateDuzp()
                && (null != getPlneni() && getPlneni().compareTo(BigDecimal.ZERO) > 0)
                && StringUtils.isNotBlank(getText()));
    }
}
