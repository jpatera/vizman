package eu.japtor.vizman.backend.entity;

import eu.japtor.vizman.ui.components.ArchIconBox;
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
//@SequenceGenerator(initialValue = 1, name = "id_gen", sequenceName = "zak_seq")
public class Zak extends AbstractGenIdEntity implements KzTreeAware, HasItemType, HasArchState, HasModifDates {

//    @GeneratedValue(generator = "uuid2")
//    @GenericGenerator(name = "uuid2", strategy = "uuid2")
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
    @Column(name = "DATE_CREATE")
    @CreationTimestamp
    private LocalDate dateCreate;

    @Basic
    @Column(name = "DATETIME_UPDATE")
    private LocalDateTime datetimeUpdate;

    @Basic
    @Column(name = "POZNAMKA")
    private String poznamka;

    // TODO: try LAZY - for better performance in TreeGrid ?
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "ID_KONT")
    private Kont kont;

    @OneToMany(mappedBy = "zak", fetch = FetchType.EAGER, cascade = CascadeType.REFRESH, orphanRemoval = false)
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
    public void setPoznamka(String poznamka) {
        this.poznamka = poznamka;
    }

    @Transient
    public ArchIconBox.ArchState getArchState() {
        return (null == arch) || !arch ? ArchIconBox.ArchState.ACTIVE : ArchIconBox.ArchState.ARCHIVED;
    }

//    public List<ZakDoc> getZakDocs() {
//        return zakDocs;
//    }

    public List<Fakt> getFakts() {
        return fakts;
    }

//    public void setFakts(List<Fakt> fakts) {
//        this.fakts = fakts;
//    }
//
//    public void addFakt(Fakt fakt) {
//        fakts.add(fakt);
//        fakt.setZak(this);
//    }
//
//    public void addFaktOnTop(Fakt fakt) {
//        fakts.add(0, fakt);
//        fakt.setZak(this);
//    }
//
//    public void removeFakt(Fakt fakt) {
//        fakts.remove(fakt);
//        fakt.setZak(null);
//    }

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


    public Long getKontId() {
        return getKont().getId();
    }

//    @Transient
    public String getCkont() {
        return null == kont ? "" : kont.getCkont();
    }

//    @Transient
    public String getKontText() {
        return null == kont ? "KONT-TEXT" : kont.getText();
    }

//    @Transient
    public String getKzCislo() {
        StringBuilder builder = new StringBuilder();
        String ckont = null  == kont ? "" : kont.getCkont();
        builder .append(ckont)
                .append(StringUtils.isBlank(ckont) ? "" : " / ")
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

//    @Transient
    public String getKlientName() {
        return null == kont ? null : kont.getKlientName();
    }

//    @Transient
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

//    @Transient
    public BigDecimal getHonorarHrubyNotNull() {
        BigDecimal honorarHruby = getHonorarHruby();
        return null == honorarHruby ? BigDecimal.ZERO : honorarHruby;
    }

//    @Transient
    public Integer getNewCfakt() {
        return getLastCfakt() + 1;
    }

//    @Transient
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
    public Klient getKlient() {
        return null;
    }

//    @Transient
//    public int getFaktsOver() {
//        int over = 0;
//        List<Fakt> fakts = getFakts();
//        for (Fakt fakt : fakts) {
//            if (null != fakt.getDateDuzp() && fakt.getDateDuzp().isAfter(LocalDate.now())) {
//                over++;
//            }
//        }
//        return over;
//    }

//    @Transient
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
//        this.honorar = BigDecimal.valueOf(0);
        this.kont = parentKont;
        this.arch = false;

//        UUID uuid = UUID.randomUUID();
//        String randomUUIDString = uuid.toString();
    }


//    public void updateBasicData(Zak zak) {
//
//        this.setVersion(zak.getVersion());
//        this.uuid = zak.uuid;
//        this.typ = zak.typ;
//        this.ckontOrig = zak.ckontOrig;
//        this.czak = zak.czak;
//        this.rok = zak.rok;
//        this.arch = zak.arch;
//        this.skupina = zak.skupina;
//        this.text = zak.text;
//        this.folder = zak.folder;
//        this.honorar = zak.honorar;
////        this.setKontId(zak.getKontId());
//        this.tmp = zak.tmp;
//        this.dateCreate = zak.dateCreate;
//        this.rokmeszad = zak.rokmeszad;
////        this.dateCreate = kont.dateCreate;
//        this.datetimeUpdate = zak.datetimeUpdate;
//
////        BigDecimal rozprac;
//    }


//    public List<Fakt> getFakts() {
//        if (null == fakts) {
//            return Collections.emptyList();
//        }
//        return fakts;
//    }


//    @Override
//    public void setNodes(List<KzTreeAware> nodes) {
////        return new ArrayList();
//    }

//    @Override
//    public void setNodes(Set<? extends KzTreeAware> zaks) {
//        // Do nothing
//    }

//    @Override
//    public String getObjednatel() {
//        return "";
//    }

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
