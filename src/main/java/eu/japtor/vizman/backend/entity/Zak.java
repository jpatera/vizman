package eu.japtor.vizman.backend.entity;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.function.Predicate;

@Entity
@Table(name = "ZAK")
//@SequenceGenerator(initialValue = 1, name = "id_gen", sequenceName = "zak_seq")
public class Zak extends AbstractGenIdEntity implements KzTreeAware, HasItemType, HasModifDates {

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
    @Column(name = "HONORAR")
    private BigDecimal honorar;

    @Basic
    @Column(name = "ROZPRAC")
    private BigDecimal rozprac;

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

    // TODO: try LAZY - for better performance in TreeGrid ?
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "ID_KONT")
    private Kont kont;

    @OneToMany(mappedBy = "zak", fetch = FetchType.EAGER)
    @OrderBy("cfakt DESC")
    private List<Fakt> fakts = new ArrayList<>();

    @OneToMany(mappedBy = "zak")
    @LazyCollection(LazyCollectionOption.FALSE)
    private List<ZakDoc> zakDocs = new ArrayList<>();

    @Basic
    @Column(name = "R_ZAL")
    private Integer rZal;

    @Basic
    @Column(name = "R1")
    private BigDecimal r1;

    @Basic
    @Column(name = "R2")
    private BigDecimal r2;

    @Basic
    @Column(name = "R3")
    private BigDecimal r3;

    @Basic
    @Column(name = "R4")

    private BigDecimal r4;

    @Basic
    @Column(name = "RM")
    private BigDecimal rm;

    @Transient
    private boolean checked;



    public String getCkontOrig() {
        return ckontOrig;
    }

    public void setCkontOrig(String ckontOrig) {
        this.ckontOrig = ckontOrig;
    }

    public Integer getCzak() {
        return czak;
    }

    @Transient
    @Override
    public Long getItemId() {
        return getId();
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

//    @Basic
//    @Column(name = "TYP_DOKLADU")
//    public String getTypDokladu() {
//        return typDokladu;
//    }
//
//    public void setTypDokladu(String typDokladu) {
//        this.typDokladu = typDokladu;
//    }

    @Override
    public Integer getRok() {
        return rok;
    }

    public void setRok(Integer rokzak) {
        this.rok = rokzak;
    }

    public String getRokmeszad() {
        return rokmeszad;
    }

    public void setRokmeszad(String rokmeszad) {
        this.rokmeszad = rokmeszad;
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

    public BigDecimal getHonorar() {
        return honorar;
    }

    public void setHonorar(BigDecimal honorar) {
        this.honorar = honorar;
    }


    @Override
    public Mena getMena() {
        return null == getKont() ? null : getKont().getMena();
    }




    public BigDecimal getRozprac() {
        return rozprac;
    }

    public void setRozprac(BigDecimal rozprac) {
        this.rozprac = rozprac;
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

    public Integer getrZal() {
        return rZal;
    }

    public void setrZal(Integer rZal) {
        this.rZal = rZal;
    }

    public BigDecimal getR1() {
        return r1;
    }

    public void setR1(BigDecimal r1) {
        this.r1 = r1;
    }

    public BigDecimal getR2() {
        return r2;
    }

    public void setR2(BigDecimal r2) {
        this.r2 = r2;
    }

    public BigDecimal getR3() {
        return r3;
    }

    public void setR3(BigDecimal r3) {
        this.r3 = r3;
    }

    public BigDecimal getR4() {
        return r4;
    }

    public void setR4(BigDecimal r4) {
        this.r4 = r4;
    }

    public String getSkupina() {
        return skupina;
    }

    public void setSkupina(String skupina) {
        this.skupina = skupina;
    }

    public BigDecimal getRm() {
        return rm;
    }

    public void setRm(BigDecimal rm) {
        this.rm = rm;
    }


    public Kont getKont() {
        return kont;
    }

    public void setKont(Kont kont) {
        this.kont = kont;
    }

    public List<ZakDoc> getZakDocs() {
        return zakDocs;
    }

    public List<Fakt> getFakts() {
        return fakts;
    }
    public void setFakts(List<Fakt> fakts) {
        this.fakts = fakts;
    }


    @Transient
    public Long getKontId() {
        return getKont().getId();
    }

    @Transient
    public String getCkont() {
        return null == kont ? "" : kont.getCkont();
    }

    @Transient
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
        String kontText = null == kont ? "" : kont.getText() == null ? "" : kont.getText().substring(0, 25);
        builder .append(kontText)
                .append(" / ")
                .append(null == text ? "" : czak)
        ;
        String result = builder.toString();
        return StringUtils.isBlank(result) ? null : result;
    }

    @Transient
    public String getKlientName() {
        return null == kont ? null : kont.getKlientName();
    }

    @Transient
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
        return
            getHonorarNotNull().add(
                fakts.stream()
                    .filter(fakt -> fakt.getTyp() == ItemType.SUB)
                    .map(fakt -> fakt.getCastka())
                    .reduce(BigDecimal.ZERO, BigDecimal::add)
            );
    }

    @Transient
    public BigDecimal getHonorarNotNull() {
        return null == honorar ? BigDecimal.ZERO : honorar;
    }


    @Transient
    public Integer getNewCfakt() {
        return getLastCfakt() + 1;
    }

    @Transient
    public BigDecimal getSumPlneni() {
        return getFakts().stream()
                .map(fakt -> fakt.getPlneni())
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public Zak() {
        this(ItemType.ZAK, 9999, null);
    }

    public Zak(ItemType typ, Integer czak, Kont parentKont) {
        super();
        this.typ = typ;
        this.czak = czak;
        this.rok = LocalDate.now().getYear();
        this.uuid = UUID.randomUUID();
        this.honorar = BigDecimal.valueOf(0);
        this.kont = parentKont;
        this.arch = false;

        UUID uuid = UUID.randomUUID();
        String randomUUIDString = uuid.toString();
    }

// -----------------------------

    @Override
    public List<KzTreeAware> getNodes() {
        return new ArrayList();
    }

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

    @Override
    public Klient getKlient() {
        return null;
    }

    @Transient
    public int getFaktsOver() {
        int over = 0;
        List<Fakt> fakts = getFakts();
        for (Fakt fakt : fakts) {
            if (null != fakt.getDateDuzp() && fakt.getDateDuzp().isAfter(LocalDate.now())) {
                over++;
            }
        }
        return over;
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

// ==================================================
//
//    @Override
//    public int hashCode() {
//        if (getId() == null) {
//            return super.hashCode();
//        }
////        return 31 + getId().hashCode();
//
//        int result = (int) (getId() ^ (getId() >>> 32));
//        result = 31 * result + (int) (kont.getId() ^ (kont.getId() >>> 32));
////        result = 31 * result + (int) (z ^ (z >>> 32));
//        return result;
//    }
//
//    @Override
//    public boolean equals(Object other) {
//        if (this == other) {
//            return true;
//        }
//        if (getId() == null) {
//            // New entities are only equal if the instance is the same
//            return super.equals(other);
//        }
//        if (!(other instanceof Zak)) {
//            return false;
//        }
//        return (getId().equals(((Zak) other).getId()))
//                && (kont.getId().equals(((Zak) other).kont.getId()));
//    }

//    @Override
//    public boolean equals(Object o) {
//        if (this == o) return true;
//        if (o == null || getClass() != o.getClass()) return false;
//
//        Zak zakentity = (Zak) o;
//
//        if (id != zakentity.id) return false;
//        if (cisloZakazky != null ? !cisloZakazky.equals(zakentity.cisloZakazky) : zakentity.cisloZakazky != null)
//            return false;
//        if (idZakazky != null ? !idZakazky.equals(zakentity.idZakazky) : zakentity.idZakazky != null) return false;
//        if (typDokladu != null ? !typDokladu.equals(zakentity.typDokladu) : zakentity.typDokladu != null) return false;
//        if (rokzak != null ? !rokzak.equals(zakentity.rokzak) : zakentity.rokzak != null) return false;
//        if (rokmeszad != null ? !rokmeszad.equals(zakentity.rokmeszad) : zakentity.rokmeszad != null) return false;
//        if (text != null ? !text.equals(zakentity.text) : zakentity.text != null) return false;
//        if (x != null ? !x.equals(zakentity.x) : zakentity.x != null) return false;
//        if (honorar != null ? !honorar.equals(zakentity.honorar) : zakentity.honorar != null) return false;
//        if (rozprac != null ? !rozprac.equals(zakentity.rozprac) : zakentity.rozprac != null) return false;
//        if (tmp != null ? !tmp.equals(zakentity.tmp) : zakentity.tmp != null) return false;
//        if (arch != null ? !arch.equals(zakentity.arch) : zakentity.arch != null) return false;
//        if (rZal != null ? !rZal.equals(zakentity.rZal) : zakentity.rZal != null) return false;
//        if (r1 != null ? !r1.equals(zakentity.r1) : zakentity.r1 != null) return false;
//        if (r2 != null ? !r2.equals(zakentity.r2) : zakentity.r2 != null) return false;
//        if (r3 != null ? !r3.equals(zakentity.r3) : zakentity.r3 != null) return false;
//        if (r4 != null ? !r4.equals(zakentity.r4) : zakentity.r4 != null) return false;
//        if (skupina != null ? !skupina.equals(zakentity.skupina) : zakentity.skupina != null) return false;
//        if (rm != null ? !rm.equals(zakentity.rm) : zakentity.rm != null) return false;
//
//        return true;
//    }
//
//    @Override
//    public int hashCode() {
//        int result = (int) (id ^ (id >>> 32));
//        result = 31 * result + (cisloZakazky != null ? cisloZakazky.hashCode() : 0);
//        result = 31 * result + (idZakazky != null ? idZakazky.hashCode() : 0);
//        result = 31 * result + (typDokladu != null ? typDokladu.hashCode() : 0);
//        result = 31 * result + (rokzak != null ? rokzak.hashCode() : 0);
//        result = 31 * result + (rokmeszad != null ? rokmeszad.hashCode() : 0);
//        result = 31 * result + (text != null ? text.hashCode() : 0);
//        result = 31 * result + (x != null ? x.hashCode() : 0);
//        result = 31 * result + (honorar != null ? honorar.hashCode() : 0);
//        result = 31 * result + (rozprac != null ? rozprac.hashCode() : 0);
//        result = 31 * result + (tmp != null ? tmp.hashCode() : 0);
//        result = 31 * result + (arch != null ? arch.hashCode() : 0);
//        result = 31 * result + (rZal != null ? rZal.hashCode() : 0);
//        result = 31 * result + (r1 != null ? r1.hashCode() : 0);
//        result = 31 * result + (r2 != null ? r2.hashCode() : 0);
//        result = 31 * result + (r3 != null ? r3.hashCode() : 0);
//        result = 31 * result + (r4 != null ? r4.hashCode() : 0);
//        result = 31 * result + (skupina != null ? skupina.hashCode() : 0);
//        result = 31 * result + (rm != null ? rm.hashCode() : 0);
//        return result;
//    }

// ======================================

}
