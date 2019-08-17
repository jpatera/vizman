package eu.japtor.vizman.backend.entity;

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
//@SequenceGenerator(initialValue = 1, name = "id_gen", sequenceName = "kont_seq")
public class Kont extends AbstractGenIdEntity implements KzTreeAware, HasItemType, HasArchState, HasModifDates {

//    public static final GrammarGender GENDER = GrammarGender.MASCULINE;
//    public static final String NOMINATIVE_SINGULAR = "Kontrakt";
//    public static final String NOMINATIVE_PLURAL = "Kontrakty";
//    public static final String GENITIVE_SINGULAR = "Kontraktu";
//    public static final String GENITIVE_PLURAL = "Kontraktů";
//    public static final String ACCUSATIVE_SINGULAR = "Kontrakt";
//    public static final String ACCUSATIVE_PLURAL = "Kontrakty";

//    @GeneratedValue(generator = "uuid2")
//    @GenericGenerator(name = "uuid2", strategy = "uuid2")
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

    @Basic
    @Column(name = "INVESTOR")
    private String investor;

    @Basic
    @Column(name = "OBJEDNATEL")
    private String objednatel;

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
    @JoinColumn(name = "ID_KLIENT")
    private Klient klient;

    public Klient getKlient() {
        return klient;
    }
    public void setKlient(Klient klient) {
        this.klient = klient;
    }

    @Transient
    private boolean checked;

    @Transient
    public String getKlientName() {
        return null == klient ? "" : (null == klient.getName() ? "" : klient.getName());
    }

    @Override
    public ItemType getTyp() {
        return typ;
    }
    public void setTyp(ItemType typ) {
        this.typ = typ;
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

    public String getInvestor() {
        return investor;
    }
    public void setInvestor(String investor) {
        this.investor = investor;
    }

    public String getObjednatel() {
        return objednatel;
    }
    public void setObjednatel(String objednatel) {
        this.objednatel = objednatel;
    }

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
    @Override
    public Long getItemId() {
        return getId();
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
    @Override
    public BigDecimal getHonorar() {
        return getNodes().stream()
                .map(node -> node.getHonorar())
                .reduce(BigDecimal.ZERO, BigDecimal::add);
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
//    public Collection<T extends KzTreeAware> getNodes() {
//        if (null != this.zaks) {
        return this.zaks;
//        return (Collection<KzTreeAware>)(this.zaks);
//        return new ArrayList();
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

    public void updateBasicData(Kont kont) {

        this.setVersion(kont.getVersion());
        this.uuid = kont.uuid;
        this.typ = kont.typ;
        this.ckont = kont.ckont;
        this.rok = kont.rok;
//        this.arch = kont.arch;
        this.investor = kont.investor;
        this.objednatel = kont.objednatel;
        this.mena = kont.mena;
        this.text = kont.text;
        this.folder = kont.folder;
        this.klient = kont.klient;
//        this.dateCreate = kont.dateCreate;
        this.datetimeUpdate = kont.datetimeUpdate;

//        BigDecimal rozprac;
    }

//    @Override
//    public void setNodes(List<KzTreeAware> subNodes) {
//
//    }

//    @Override
//    public void setNodes(List<? extends KzTreeAware> nodes) {
//        this.zaks = nodes;
//    }

// ========================================

    @Override
    public int hashCode() {
		return uuid.hashCode();
//        if (getId() == null) {
//            return super.hashCode();
//        }
//        return 31 + getId().hashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (!(other instanceof AbstractGenIdEntity)) return false;
        return getId() != null && getId().equals(((AbstractGenIdEntity) other).getId());
//		if (id == null) {
//			// New entities are only equal if the instance is the same
//			return super.equals(other);
//		}
    }

//    @Override
//    public Boolean getArch() {
//        return null;
//    }
}
