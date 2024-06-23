package eu.japtor.vizman.backend.entity;

import eu.japtor.vizman.backend.utils.VzmFileUtils;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.math.BigDecimal;
import java.nio.file.Paths;
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

    @Basic
    @Column(name = "UPDATED_BY")
    private String updatedBy;

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

    @Override
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

    public void setDatetimeUpdate(LocalDateTime datetimeUpdate) {
        this.datetimeUpdate = datetimeUpdate;
    }


    public String getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
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
    public String getCkzText() {
        return getCkont() + "-" + getCzak()
                + (null == zak.getText() ? "" : " , " + zak.getText());
    }

    @Transient
    public String getFaktExpPath(final String docRootServer) {
        return Paths.get(
                docRootServer
                , null == zak.getKontFolder() ? "XXXXX.X_KONT-FOLDER" : zak.getKontFolder()
                , null == zak.getFolder() ? "X_ZAK-FOLDER" : zak.getFolder()
                , "Faktury"
                , getFaktExpFileName()
        ).toString();
    }

    @Transient
    public String getFaktExpData() {
        return getCkont()
                + "\n" + getKontText()
                + "\n" + getCzak() + " - " + getZakText()
                + "\n" + cfakt + " - " + text + "\t" + castka + "\t" + getMena().toString()
                       + "\t" + dateDuzp.toString() + "\t" + dateVystav.toString()
        ;
    }

    @Transient
    public String getFaktExpFileName() {
        int MAX_CHAR = 15;
        String kontText = (StringUtils.isEmpty(getKontText()) ? "NO-KONT-TEXT" : VzmFileUtils.normalizeDirFileName(getKontText()));
        String zakText = (StringUtils.isEmpty(getZakText()) ? "NO-KONT-TEXT" : VzmFileUtils.normalizeDirFileName(getZakText()));
        String plnText = (StringUtils.isEmpty(text) ? "NO-PLN-TEXT" : VzmFileUtils.normalizeDirFileName(text));
        return getCkont()
                + "_" + StringUtils.substring(kontText, 0, MAX_CHAR)
                + "_" + getCzak()
                + "-" + StringUtils.substring(zakText, 0, MAX_CHAR)
                + "_" + cfakt
                + "-" + StringUtils.substring(plnText, 0, MAX_CHAR)
                + ".txt"
                ;
    }

    @Transient
    public String getCkont() {
        return null == zak ? "" : zak.getCkont();
    }

    @Transient
    public String getKontText() {
        return null == zak ? "KONT-TEXT" : zak.getKontText();
    }

    @Transient
    public String getZakText() {
        return null == zak ? "ZAK-TEXT" : zak.getText();
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
