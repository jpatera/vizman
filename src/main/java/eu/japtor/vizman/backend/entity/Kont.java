package eu.japtor.vizman.backend.entity;

import eu.japtor.vizman.ui.components.ArchIconBox;
import eu.japtor.vizman.ui.components.DigiIconBox;
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


@Entity
@Table(name = "KONT")
public class Kont extends AbstractGenIdEntity implements KzTreeAware, HasItemType, HasArchState, HasModifDates {

    @Column(columnDefinition = "BINARY(16)")
    private UUID uuid;

    @Enumerated(EnumType.STRING)
    @Basic
    @Column(name = "TYP")
    private ItemType typ;

    @Basic
    @Column(name = "CKONT")
    private String ckont;

    @Basic
    @Column(name = "ROK")
    private Integer rok;

    @Basic
    @Column(name = "TEXT")
    private String text;

    @Basic
    @Column(name = "FOLDER")
    private String folder;


    // TODO: remove when not needed anymore
    @Basic
    @Column(name = "INVESTOR")
    private String investorOrig;

//    @Basic
//    @Column(name = "OBJEDNATEL")
//    private String objednatel;

    @Column(name = "MENA")
    @Enumerated(EnumType.STRING)
    private Mena mena;

    @Basic
    @Column(name = "DATE_CREATE")
    @CreationTimestamp
    private LocalDate dateCreate;

    @Basic
    @Column(name = "DATETIME_UPDATE")
    private LocalDateTime datetimeUpdate;

    //    @OneToMany(fetch = FetchType.LAZY)
//    @OneToMany(mappedBy = "kont", fetch = FetchType.EAGER, cascade = {CascadeType.PERSIST, CascadeType.MERGE} )
//    @OneToMany(mappedBy = "kont", fetch = FetchType.EAGER, cascade = CascadeType.REMOVE, orphanRemoval = false)
    @OneToMany(mappedBy = "kont", fetch = FetchType.EAGER, cascade = CascadeType.REFRESH, orphanRemoval = false)
    @OrderBy("czak DESC")
    private List<Zak> zaks = new ArrayList<>();
//    @JoinTable(
//            name = "kont_zak",
//            joinColumns = @JoinColumn(
//                    name = "person_id", referencedColumnName = "id"),
//            inverseJoinColumns = @JoinColumn(
//                    name = "role_id", referencedColumnName = "id"))

    @OneToMany(mappedBy = "kont", cascade = CascadeType.ALL, orphanRemoval = false)
    @LazyCollection(LazyCollectionOption.FALSE)
    private List<KontDoc> kontDocs = new ArrayList<>();


    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "ID_OBJEDNATEL")
    private Klient objednatel;

    public Klient getObjednatel() {
        return objednatel;
    }
    public void setObjednatel(Klient objednatel) {
        this.objednatel = objednatel;
    }

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "ID_INVESTOR")
    private Klient investor;

    public Klient getInvestor() {
        return investor;
    }
    public void setInvestor(Klient investor) {
        this.investor = investor;
    }

    @Transient
    private boolean checked;

    @Transient
    public String getObjednatelName() {
        return null == objednatel ? "" : (null == objednatel.getName() ? "" : objednatel.getName());
    }

    @Transient
    public String getInvestorName() {
        return null == investor ? "" : (null == investor.getName() ? "" : investor.getName());
    }

    @Override
    public String getInvestorOrigName() {
        return null == investorOrig ? "" : investorOrig;
    }

    @Override
    public ItemType getTyp() {
        return typ;
    }
    public void setTyp(ItemType typ) {
        this.typ = typ;
    }

// ----------------------------------------------------------

    public static Kont getEmptyInstance() {
        Kont k = new Kont();
        k.setCkont(null);
        k.setRok(null);
        k.setObjednatel(null);
        return k;
    }

    public String getCkont() {
        return ckont;
    }
    public void setCkont(String czak) {
        this.ckont = czak;
    }

    public Integer getRok() {
        return rok;
    }
    public void setRok(Integer rok) {
        this.rok =  rok;
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

    public String getInvestorOrig() {
        return investorOrig;
    }
    public void setInvestorOrig(String investorOrig) {
        this.investorOrig = investorOrig;
    }

//    public String getObjednatelName() {
//        return objednatel;
//    }
//    public void setObjednatelName(String objednatel) {
//        this.objednatel = objednatel;
//    }

    public Mena getMena() {
        return mena;
    }
    public void setMena(Mena mena) {
        this.mena = mena;
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


    public List<KontDoc> getKontDocs() {
        return kontDocs;
    }
    public void setKontDocs(List<KontDoc> kontDocs) {
        this.kontDocs = kontDocs;
    }

    public List<Zak> getZaks() {
        return zaks;
    }
    public void setZaks(List<Zak> zaks) {
        this.zaks = zaks;
    }


    public void addZak(Zak zak) {
        zaks.add(zak);
        zak.setKont(this);
    }

    public void addZakOnTop(Zak zak) {
        zaks.add(0, zak);
        zak.setKont(this);
    }

    public void removeZak(Zak zak) {
        zaks.remove(zak);
        zak.setKont(null);
    }


    @Transient
    @Override
    public ArchIconBox.ArchState getArchState() {
        if ((null == getZaks()) || (getZaks().size() == 0)) {
            return ArchIconBox.ArchState.EMPTY;
        } else if (getZaks().stream().allMatch(zak -> zak.getArch())) {
            return ArchIconBox.ArchState.ARCHIVED;
        } else {
            return ArchIconBox.ArchState.ACTIVE;
        }
    }

    @Transient
    @Override
    public DigiIconBox.DigiState getDigiState() {
        if ((null == getZaks()) || (getZaks().size() == 0)) {
            return DigiIconBox.DigiState.EMPTY;
        } else if (getZaks().stream().anyMatch(zak -> zak.getDigi())) {
            return DigiIconBox.DigiState.DIGI_ONLY;
        } else {
            return DigiIconBox.DigiState.PAPER_AND_DIGI;
        }
    }

    @Transient
    @Override
    public long getBeforeTerms() {
        return getZaks().stream()
                .map(Zak::getBeforeTerms).mapToLong(Long::longValue).sum();
    }

    @Transient
    @Override
    public long getAfterTerms() {
        return getZaks().stream()
                .map(Zak::getAfterTerms).mapToLong(Long::longValue).sum();
    }

    @Transient
    @Override
    public Integer getCzak() {
        return null;
    }

    @Transient
    public Integer getLastCzak() {
        return getZaks().stream()
                .mapToInt(zak -> zak.getCzak())
                .max().orElse(0);
    }

    @Transient
    public Integer getNewCzak() {
        return getLastCzak() + 1;
    }

    @Transient
    public BigDecimal getHonorarHruby() {
        return getNodes().stream()
                .map(node -> node.getHonorarHruby())
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Transient
    public BigDecimal getHonorarCisty() {
        return getNodes().stream()
                .map(node -> node.getHonorarCisty())
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Transient
    @Override
    public String getSkupina() {
        return null;
    }


    @Transient
    @Override
    public boolean isChecked() {
        return checked;
    }

    @Override
    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    @Transient
    @Override
    public List<Zak> getNodes() {
        return this.zaks;
    }

// ========================================

    public Kont() {
        super();
    }

    public Kont(final ItemType itemType) {
        super();
//        this.uuid = uuid;
        this.typ = itemType;
        this.rok = LocalDate.now().getYear();
        this.mena = Mena.CZK;
        this.uuid = UUID.randomUUID();
        // TODO: move it to abstract class and create field/column there
        // Use it in hash
//        UUID uuid = UUID.randomUUID();
//        String randomUUIDString = uuid.toString();
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
