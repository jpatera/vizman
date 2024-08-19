package eu.japtor.vizman.backend.entity;

import eu.japtor.vizman.backend.utils.VzmUtils;
import eu.japtor.vizman.ui.components.ArchIconBox;
import eu.japtor.vizman.ui.components.DigiIconBox;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Predicate;

@Entity
@Table(name = "ZAK")
public class Zak extends AbstractGenIdEntity implements KzTreeAware, HasItemType, HasArchState
        , HasModifDates, HasUpdatedBy, HasAlertModif
{

    @Column(columnDefinition = "BINARY(16)")
    private UUID uuid;

    @Enumerated(EnumType.STRING)
    @Basic
    @Column(name = "TYP")
    private ItemType typ;

    @Basic
    @Column(name = "CKONT_ORIG")
    private String ckontOrig;

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
    @Column(name = "ROKMESZAD")
    private String rokmeszad;

    @Basic
    @Column(name = "TEXT")
    private String text;

    @Basic
    @Column(name = "FOLDER")
    private String folder;

    @Basic
    @Column(name = "TMP")
    private String tmp;

    @Basic
    @Column(name = "ARCH")
    private Boolean arch;

    @Basic
    @Column(name = "DIGI")
    private Boolean digi;

    @Basic
    @Column(name = "DATE_CREATE")
    @CreationTimestamp
    private LocalDate dateCreate;

    // Automatic date update is implemented in database
    @Basic
    @Column(name = "DATETIME_UPDATE", insertable = false, updatable = false)
    private LocalDateTime datetimeUpdate;

    @Basic
    @Column(name = "UPDATED_BY")
    private String updatedBy;

    @Column(name = "ALERT_MODIF")
    private Boolean alertModif;

    @Basic
    @Column(name = "POZNAMKA")
    private String poznamka;

    // TODO: try LAZY - for better performance in TreeGrid ?
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "ID_KONT")
    private Kont kont;

    @OneToMany(mappedBy = "zak", fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = false)
    @OrderBy("cfakt DESC")
    private List<Fakt> fakts = new ArrayList<>();

    @OneToMany(mappedBy = "zak")
    @LazyCollection(LazyCollectionOption.FALSE)
    private List<ZakDoc> zakDocs = new ArrayList<>();

    @Transient
    private boolean checked;

    public Integer getCzak() {
        return czak;
    }

    public void setCzak(Integer czak) {
        this.czak = czak;
    }

    public ItemType getTyp() {
        return typ;
    }

    public void setTyp(ItemType typ) {
        this.typ = typ;
    }

    @Override
    public Integer getRok() {
        return rok;
    }
    public void setRok(Integer rokzak) {
        this.rok = rokzak;
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

    public String getTmp() {
        return tmp;
    }
    public void setTmp(String tmp) {
        this.tmp = tmp;
    }

    public Boolean getArch() {
        return arch;
    }
    public void setArch(Boolean arch) {
        this.arch = arch;
    }


    public Boolean getDigi() {
        return digi;
    }
    public void setDigi(Boolean digi) {
        this.digi = digi;
    }

    @Override public LocalDate getDateCreate() {
        return dateCreate;
    }
    public void setDateCreate(LocalDate dateCreate) {
        this.dateCreate = dateCreate;
    }

    @Override
    public LocalDateTime getDatetimeUpdate() {
        return datetimeUpdate;
    }
    public void setDatetimeUpdate(LocalDateTime datetimeUpdate) {
        this.datetimeUpdate = datetimeUpdate;
    }

    @Override
//    @Transient
    public String getUpdatedBy() {
        return updatedBy;
    }
    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
        if (VzmUtils.isAlertModifCondition(updatedBy)) {
            setAlertModif(true);
        }
    }

    public String getPoznamka() {
        return poznamka;
    }
    public void setPoznamka(String poznamka) {
        this.poznamka = poznamka;
    }

    @Override
    public boolean isAlertModif() {
        return null != alertModif && alertModif;
    }
    public void setAlertModif(Boolean alertModif) {
        this.alertModif = alertModif;
    }

    @Transient
    public boolean hasAlertedItems() {
        return getFakts().stream()
                .map(f -> f.isAlertModif())
                .anyMatch(a -> a);
    }

    @Transient
    public boolean isAlerted() {
        return isAlertModif() || hasAlertedItems();
    }

    @Transient
    public ArchIconBox.ArchState getArchState() {
        return (null == arch) || !arch ? ArchIconBox.ArchState.ACTIVE : ArchIconBox.ArchState.ARCHIVED;
    }

    @Transient
    public DigiIconBox.DigiState getDigiState() {
        return (null == digi) || !digi ? DigiIconBox.DigiState.PAPER_AND_DIGI : DigiIconBox.DigiState.DIGI_ONLY;
    }

    public List<Fakt> getFakts() {
        return fakts;
    }

    public Kont getKont() {
        return kont;
    }
    public void setKont(Kont kont) {
        this.kont = kont;
    }


    public String getSkupina() {
        return skupina;
    }
    public void setSkupina(String skupina) {
        this.skupina = skupina;
    }



    // Transient fields
    // ================

    public Long getKontId() {
        return getKont().getId();
    }

    public String getCkont() {
        return null == kont ? "" : kont.getCkont();
    }

    public String getKontText() {
        return null == kont ? "KONT-TEXT" : kont.getText();
    }

    public String getKzCislo() {
        StringBuilder builder = new StringBuilder();
        String ckont = null  == kont ? "" : kont.getCkont();
        builder .append(ckont)
                .append(StringUtils.isBlank(ckont) ? "" : "-")
                .append(null == czak ? "" : czak)
        ;
        return builder.toString();
    }

    @Transient
    public String getKzText() {
        StringBuilder builder = new StringBuilder();
        String kontText = null == kont ? "" : kont.getText() == null ? "" : StringUtils.substring(kont.getText(), 0, 25);
        builder .append(kontText)
                .append(" / ")
                .append(null == text ? "" : text)
        ;
        String result = builder.toString();
        return StringUtils.isBlank(result) ? null : result;
    }

    @Transient
    public String getKzCisloTextForRep() {
        return "Zakázka: " + getKzCislo()
                + " \u00A0\u00A0\u00A0\u00A0 Měna: " + getMena().toString();
    }


    @Override
    @Transient
    public Mena getMena() {
        return null == kont ? null : kont.getMena();
    }

    public String getObjednatelName() {
        return null == kont ? null : kont.getObjednatelName();
    }

    public String getInvestorName() {
        return null == kont ? null : kont.getInvestorName();
    }

    public String getInvestorOrigName() {
        return null == kont ? null : kont.getInvestorOrigName();
    }

    public String getKontFolder() {
        return null == kont ? null : kont.getFolder();
    }

    @Transient
    private Predicate<Fakt> afterTermsPredicate = fakt ->
        ((null == fakt.getDateVystav()) && (null != fakt.getDateDuzp()) && (fakt.getDateDuzp().plusDays(1).isBefore(LocalDate.now())))
    ;

    @Transient
    private Predicate<Fakt> beforeTermsPredicate = fakt ->
        ((null == fakt.getDateVystav()) && (null != fakt.getDateDuzp()) && (fakt.getDateDuzp().isAfter(LocalDate.now())))
    ;

    @Transient
    @Override
    public long getBeforeTerms() {
        return getFakts().stream()
                .filter(beforeTermsPredicate).count();
    }

    @Transient
    @Override
    public long getAfterTerms() {
        return getFakts().stream()
                .filter(afterTermsPredicate).count();
    }

    @Transient
    public Integer getLastCfakt() {
        return getFakts().stream()
                .mapToInt(fakt -> fakt.getCfakt())
                .max().orElse(0);
    }

    @Transient
    public BigDecimal getHonorarCisty() {
        return getHonorarHrubyNotNull().add(
                fakts.stream()
                    .filter(fakt -> fakt.getTyp() == ItemType.SUB)
                    .map(fakt -> fakt.getCastka())
                    .reduce(BigDecimal.ZERO, BigDecimal::add)
        );
    }

    @Transient
    public BigDecimal getHonorarHruby() {
        return fakts.stream()
                .filter(fakt -> fakt.getTyp() == ItemType.FAKT)
                .map(fakt -> null == fakt.getCastka() ? BigDecimal.ZERO : fakt.getCastka())
                .reduce(BigDecimal.ZERO, BigDecimal::add)
        ;
    }

    public BigDecimal getHonorarHrubyNotNull() {
        BigDecimal honorarHruby = getHonorarHruby();
        return null == honorarHruby ? BigDecimal.ZERO : honorarHruby;
    }

    public Integer getNewCfakt() {
        return getLastCfakt() + 1;
    }

    public BigDecimal getSumPlneni() {
        return getFakts().stream()
                .map(fakt -> fakt.getPlneni())
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Override
    public List<KzTreeAware> getNodes() {
        return new ArrayList();
    }

    public Zak() {
        super();
    }

    @Override
    public Klient getObjednatel() {
        return null;
    }


    @Override
    public Klient getInvestor() {
        return null;
    }

    @Override
    public boolean isChecked() {
        return checked;
    }

    @Override
    public void setChecked(boolean checked) {
        this.checked = checked;
    }

// ========================================

    public Zak(ItemType typ, Integer czak, Kont parentKont) {
        super();
        this.typ = typ;
        this.czak = czak;
        this.rok = LocalDate.now().getYear();
        this.uuid = UUID.randomUUID();
        this.kont = parentKont;
        this.arch = false;
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
