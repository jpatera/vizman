package eu.japtor.vizman.backend.entity;

import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

@Entity
@Table(name = "ZAK")
@SequenceGenerator(initialValue = 1, name = "id_gen", sequenceName = "zak_seq")
public class Zak extends AbstractGenEntity implements KzTreeAware, HasItemType, HasModifDates {

    @Enumerated(EnumType.STRING)
    private ItemType typ;

    private String ckontOrig;
    private Integer czak;
    private Short rokzak;
    private String rokmeszad;
    private String text;
    private String folder;
    private BigDecimal honorar;
    private BigDecimal rozprac;
    private String tmp;
    private Boolean arch;
    private Integer rZal;
    private BigDecimal r1;
    private BigDecimal r2;
    private BigDecimal r3;
    private BigDecimal r4;
    private String skupina;
    private BigDecimal rm;
    private LocalDate dateCreate;
    private LocalDateTime datetimeUpdate;


    public Zak() {
        this(ItemType.ZAK);
    }

    public Zak(ItemType typ) {
        super();
        this.typ = typ;
    }


    @Basic
    @Column(name = "CKONT_ORIG")
    public String getCkontOrig() {
        return ckontOrig;
    }

    public void setCkontOrig(String ckontOrig) {
        this.ckontOrig = ckontOrig;
    }

    @Basic
    @Column(name = "CZAK")
    public Integer getCzak() {
        return czak;
    }

    public void setCzak(Integer czak) {
        this.czak = czak;
    }

    @Basic
    @Column(name = "TYP")
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

    @Basic
    @Column(name = "ROKZAK")
    public Short getRokzak() {
        return rokzak;
    }

    public void setRokzak(Short rokzak) {
        this.rokzak = rokzak;
    }

    @Basic
    @Column(name = "ROKMESZAD")
    public String getRokmeszad() {
        return rokmeszad;
    }

    public void setRokmeszad(String rokmeszad) {
        this.rokmeszad = rokmeszad;
    }

    @Basic
    @Column(name = "DATE_CREATE")
    public LocalDate getDateCreate() {
        return dateCreate;
    }

    public void setDateCreate(LocalDate dateCreate) {
        this.dateCreate = dateCreate;
    }

    @Basic
    @Column(name = "DATETIME_UPDATE")
    public LocalDateTime getDatetimeUpdate() {
        return datetimeUpdate;
    }

    public void setDatetimeUpdate(LocalDateTime datetimeUpdate) {
        this.datetimeUpdate = datetimeUpdate;
    }

    @Basic
    @Column(name = "TEXT")
    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Basic
    @Column(name = "FOLDER")
    public String getFolder() {
        return folder;
    }

    public void setFolder(String docdir) {
        this.folder = docdir;
    }


    @Basic
    @Column(name = "HONORAR")
    public BigDecimal getHonorar() {
        return honorar;
    }

    public void setHonorar(BigDecimal honorar) {
        this.honorar = honorar;
    }


    @Override
    public Mena getMena() {
        return getKont().getMena();
    }

    @Basic
    @Column(name = "ROZPRAC")
    public BigDecimal getRozprac() {
        return rozprac;
    }

    public void setRozprac(BigDecimal rozprac) {
        this.rozprac = rozprac;
    }

    @Basic
    @Column(name = "TMP")
    public String getTmp() {
        return tmp;
    }

    public void setTmp(String tmp) {
        this.tmp = tmp;
    }

    @Basic
    @Column(name = "ARCH")
    public Boolean getArch() {
        return arch;
    }

    public void setArch(Boolean arch) {
        this.arch = arch;
    }

    @Basic
    @Column(name = "R_ZAL")
    public Integer getrZal() {
        return rZal;
    }

    public void setrZal(Integer rZal) {
        this.rZal = rZal;
    }

    @Basic
    @Column(name = "R1")
    public BigDecimal getR1() {
        return r1;
    }

    public void setR1(BigDecimal r1) {
        this.r1 = r1;
    }

    @Basic
    @Column(name = "R2")
    public BigDecimal getR2() {
        return r2;
    }

    public void setR2(BigDecimal r2) {
        this.r2 = r2;
    }

    @Basic
    @Column(name = "R3")
    public BigDecimal getR3() {
        return r3;
    }

    public void setR3(BigDecimal r3) {
        this.r3 = r3;
    }

    @Basic
    @Column(name = "R4")
    public BigDecimal getR4() {
        return r4;
    }

    public void setR4(BigDecimal r4) {
        this.r4 = r4;
    }

    @Basic
    @Column(name = "SKUPINA")
    public String getSkupina() {
        return skupina;
    }

    public void setSkupina(String skupina) {
        this.skupina = skupina;
    }

    @Basic
    @Column(name = "RM")
    public BigDecimal getRm() {
        return rm;
    }

    public void setRm(BigDecimal rm) {
        this.rm = rm;
    }


    // TODO: try LAZY - for better performance in TreeGrid ?
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "ID_KONT")
    private Kont kont;

    public Kont getKont() {
        return kont;
    }

//    public void setKont(Kont kont) {
//        this.kont = kont;
//    }


    @OneToMany(mappedBy = "zak")
    @LazyCollection(LazyCollectionOption.FALSE)
    private List<ZakDoc> zakDocs;

    public List<ZakDoc> getZakDocs() {
        return zakDocs;
    }


    @OneToMany(mappedBy = "zak", fetch = FetchType.EAGER)
    private List<Fakt> fakts;

    public List<Fakt> getFakts() {
        return fakts;
    }
//    public void setFakts(List<Fakt> fakts) {
//        this.fakts = fakts;
//    }


    @Transient
    public Long getKontId() {
        return getKont().getId();
    }

    @Transient
    public String getCkont() {
        return getKont().getCkont();
    }

    @Transient
    private Predicate<Fakt> overTermsPredicate = fakt ->
            ((null != fakt.getDateVystav()) &&  (fakt.getDateDuzp().isAfter(LocalDate.now())));

    @Transient
    public Integer getOverTerms() {
        long over = getFakts().stream()
                .filter(overTermsPredicate).count();
        return new Integer((int)over);
    }

    @Transient
    public Integer getLastCfakt() {
        return getFakts().stream()
                .mapToInt(fakt -> fakt.getCfakt())
                .max().orElse(1);
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

// -----------------------------

    @Override
    public List<KzTreeAware> getNodes() {
        return new ArrayList();
    }


//    @Override
//    public void setNodes(List<KzTreeAware> nodes) {
////        return new ArrayList();
//    }

//    @Override
//    public void setNodes(Set<? extends KzTreeAware> zaks) {
//        // Do nothing
//    }

    @Override
    public String getObjednatel() {
        return "";
    }

    public int getFaktsOver() {
        int over = 0;
        List<Fakt> fakts = getFakts();
        for (Fakt fakt : fakts) {
            if (fakt.getDateDuzp().isAfter(LocalDate.now())) {
                over++;
            }
        }
        return over;
    }

// ==================================================

    @Override
    public int hashCode() {
        if (getId() == null) {
            return super.hashCode();
        }
//        return 31 + getId().hashCode();

        int result = (int) (getId() ^ (getId() >>> 32));
        result = 31 * result + (int) (kont.getId() ^ (kont.getId() >>> 32));
//        result = 31 * result + (int) (z ^ (z >>> 32));
        return result;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (getId() == null) {
            // New entities are only equal if the instance is the same
            return super.equals(other);
        }
        if (!(other instanceof Zak)) {
            return false;
        }
        return (getId().equals(((Zak) other).getId()))
                && (kont.getId().equals(((Zak) other).kont.getId()));
    }

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
