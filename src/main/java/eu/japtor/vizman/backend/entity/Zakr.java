package eu.japtor.vizman.backend.entity;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.Immutable;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Immutable
//@ReadOnly
@Entity
@Table(name = "ZAK_ROZPRAC_VIEW")
public class Zakr implements Serializable, HasItemType, HasArchState {
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "ID", updatable = false, nullable = false)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Basic
    @Column(name = "TYP")
    private ItemType typ;

    @Basic
    @Column(name = "CKONT")
    private String ckont;

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
    @Column(name = "TEXT_KONT")
    private String textKont;

    @Basic
    @Column(name = "TEXT_ZAK")
    private String textZak;

    @Enumerated(EnumType.STRING)
    @Column(name = "MENA")
    private Mena mena;

    @Basic
    @Column(name = "HONOR_CISTY")
    private BigDecimal honorCisty;

    @Basic
    @Column(name = "HONOR_FAKT")
    private BigDecimal honorFakt;

    @Basic
    @Column(name = "HONOR_SUB")
    private BigDecimal honorSub;

    @Basic
    @Column(name = "RP")
    private BigDecimal rp;  // Rozpracovanost posledni platna

    @Basic
    @Column(name = "R0")
    private BigDecimal r0;  // Rozpracovanost  na konci Q4 minuleho roku

    @Basic
    @Column(name = "R1")
    private BigDecimal r1;  // Rozpracovanost na konci Q1

    @Basic
    @Column(name = "R2")
    private BigDecimal r2;  // Rozpracovanost na konci Q2

    @Basic
    @Column(name = "R3")
    private BigDecimal r3;  // Rozpracovanost na konci Q3

    @Basic
    @Column(name = "R4")
    private BigDecimal r4;  // Rozpracovanost na konci Q4

    @Basic
    @Column(name = "OBJEDNATEL")
    private String objednatel;

    @Basic
    @Column(name = "ARCH")
    private Boolean arch;

    @Basic
    @Column(name = "ID_KONT")
    private Long idKont;

//    @OneToMany(mappedBy = "zakr", fetch = FetchType.LAZY, cascade = CascadeType.REFRESH, orphanRemoval = false)
    @OneToMany(mappedBy = "zakr", cascade = CascadeType.REFRESH, orphanRemoval = false)
    @OrderBy("rok DESC, qa DESC")
    private List<Zaqa> zaqas = new ArrayList<>();

    @Transient
    private boolean checked;

//    @Transient
//    private BigDecimal rpVysledek;

    @Transient
    public BigDecimal getRpVysledek() {
        if (null == rp || null == honorCisty) {
            return null;
        } else {
            return rp.multiply(honorCisty).divide(BigDecimal.valueOf(100L));
        }
    }

//    @Transient
//    private BigDecimal rpZbyva;

    @Transient
    public BigDecimal getRpZbyva() {
        if (null == rp || null == honorCisty) {
            return null;
        } else {
            return honorCisty.subtract(rp.multiply(honorCisty).divide(BigDecimal.valueOf(100L)));
        }
    }

//    @Transient
//    private BigDecimal ryRxVykon;

    @Transient
    public BigDecimal getRxRyVykon(String rxParam, String ryParam) {
        BigDecimal vykRx = BigDecimal.ZERO;
        BigDecimal vykRy = BigDecimal.ZERO;

        if (rxParam == null) {
            return vykRx;
        }
        switch (rxParam) {
            case "R0":
                if (null != r0 && null != honorCisty) {
                    vykRx = r0.multiply(honorCisty).divide(BigDecimal.valueOf(100L));
                }
                break;
            case "R1":
                if (null != r1 && null != honorCisty) {
                    vykRx = r1.multiply(honorCisty).divide(BigDecimal.valueOf(100L));
                }
                break;
            case "R2":
                if (null != r2 && null != honorCisty) {
                    vykRx =  r2.multiply(honorCisty).divide(BigDecimal.valueOf(100L));
                }
                break;
            case "R3":
                if (null != r3 && null != honorCisty) {
                    vykRx =  r3.multiply(honorCisty).divide(BigDecimal.valueOf(100L));
                }
                break;
            case "R4":
                if (null != r4 && null != honorCisty) {
                    vykRx = r4.multiply(honorCisty).divide(BigDecimal.valueOf(100L));
                }
                break;
            default:
        }

        if (ryParam == null) {
            return vykRy;
        }
        switch (ryParam) {
            case "R0":
                if (null != r0 && null != honorCisty) {
                    vykRy = r0.multiply(honorCisty).divide(BigDecimal.valueOf(100L));
                }
                break;
            case "R1":
                if (null != r1 && null != honorCisty) {
                    vykRy = r1.multiply(honorCisty).divide(BigDecimal.valueOf(100L));
                }
                break;
            case "R2":
                if (null != r2 && null != honorCisty) {
                    vykRy =  r2.multiply(honorCisty).divide(BigDecimal.valueOf(100L));
                }
                break;
            case "R3":
                if (null != r3 && null != honorCisty) {
                    vykRy =  r3.multiply(honorCisty).divide(BigDecimal.valueOf(100L));
                }
                break;
            case "R4":
                if (null != r4 && null != honorCisty) {
                    vykRy = r4.multiply(honorCisty).divide(BigDecimal.valueOf(100L));
                }
                break;
            default:
                if (null != rp && null != honorCisty) {
                    vykRy = rp.multiply(honorCisty).divide(BigDecimal.valueOf(100L));
                }
        }
        return vykRy.subtract(vykRx);
    }

//    public BigDecimal getFinished() {
//        return finished;
//    }
//    public void setFinished(BigDecimal finished) {
//        this.finished = finished;
//    }

//    @Transient
//    private BigDecimal finished;


    public Long getId() {
        return id;
    }

    public ItemType getTyp() {
        return typ;
    }

    public String getCkont() {
        return ckont;
    }

    public Integer getCzak() {
        return czak;
    }

    public Integer getRok() {
        return rok;
    }

    public String getSkupina() {
        return skupina;
    }

    public String getTextKont() {
        return textKont;
    }

    public String getTextZak() {
        return textZak;
    }

    public Mena getMena() {
        return mena;
    }

    public BigDecimal getHonorCisty() {
        return honorCisty;
    }
//    public void setHonorCisty(BigDecimal honorCisty) {
//        this.honorCisty = honorCisty;
//    }

    public BigDecimal getHonorFakt() {
        return honorFakt;
    }
//    public void setHonorFakt(BigDecimal honorFakt) {
//        this.honorFakt = honorFakt;
//    }

    public BigDecimal getHonorSub() {
        return honorSub;
    }
//    public void setHonorSub(BigDecimal honorSub) {
//        this.honorSub = honorSub;
//    }

    public BigDecimal getRP() {
        return rp;
    }
    public void setRP(BigDecimal rp) {
        this.rp = rp;
    }

    public BigDecimal getR0() {
        return r0;
    }
    public void setR0(BigDecimal r0) {
        this.r0 = r0;
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

    public String getObjednatel() {
        return objednatel;
    }

    public ArchIconBox.ArchState getArchState() {
        return arch ? ArchIconBox.ArchState.EMPTY.ARCHIVED : ArchIconBox.ArchState.EMPTY;
    }

    public Boolean getArch() {
        return arch;
    }

    public Long getIdKont() {
        return idKont;
    }


    // Zaqa
    // -----
    public List<Zaqa> getZaqas() {
        return zaqas;
    }

    public void setZaqas(List<Zaqa> zaqas) {
        this.zaqas = zaqas;
    }

    public void addZaqa(Zaqa zaqa) {
        zaqas.add(zaqa);
        zaqa.setZakr(this);
    }

    public void addZaqaOnTop(Zaqa zaqa) {
        zaqas.add(0, zaqa);
        zaqa.setZakr(this);
    }


    @Transient
    public boolean isChecked() {
        return checked;
    }


//    public void setId(Long id) {
//        this.id = id;
//    }
//
//    public void setTyp(ItemType typ) {
//        this.typ = typ;
//    }
//
//    public void setCkont(String ckont) {
//        this.ckont = ckont;
//    }
//
//    public void setCzak(Integer czak) {
//        this.czak = czak;
//    }
//
//    public void setSkupina(String skupina) {
//        this.skupina = skupina;
//    }
//
//    public void setRok(Integer rok) {
//        this.rok = rok;
//    }
//
//    public void setTextKont(String textKont) {
//        this.textKont = textKont;
//    }
//
//    public void setTextZak(String textZak) {
//        this.textZak = textZak;
//    }
//
//    public void setObjednatel(String objednatel) {
//        this.objednatel = objednatel;
//    }
//
//    public void setArch(Boolean arch) {
//        this.arch = arch;
//    }
//
//    public void setIdKont(Long idKont) {
//        this.idKont = idKont;
//    }

    @Transient
    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    @Transient
    public String getCkontNotNull() {
        return null == ckont ? "" : ckont;
    }

    @Transient
    public String getCzakNotNull() {
        return null == czak ? "" : czak.toString();
    }

    @Transient
    public String getTextKontNotNull() {
        return null == textKont ? "" : textKont;
    }

    @Transient
    public String getTextZakNotNull() {
        return null == textZak ? "" : textZak.toString();
    }

    @Transient
    public String getKzCislo() {
        StringBuilder builder = new StringBuilder();
        builder .append(getCkontNotNull())
                .append(" / ")
                .append(getCzakNotNull())
        ;
        return builder.toString();
    }

    @Transient
    public String getKzText() {
        StringBuilder builder = new StringBuilder();
        builder .append(StringUtils.substring(getTextKontNotNull(), 0, 25))
                .append(" / ")
                .append(getTextZakNotNull())
        ;
        return builder.toString();
    }

//    @Transient
//    @Override
//    public boolean isChecked() {
//        return checked;
//    }

//    @Override
//    public void setChecked(boolean checked) {
//        this.checked = checked;
//    }


    @Override
    public int hashCode() {
        return 31;
//		if (id == null) {
//			return super.hashCode();
//		}
//		return 31 + id.hashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (!(other instanceof AbstractGenIdEntity)) return false;
        return id != null && id.equals(((Zakr) other).id);
//		if (id == null) {
//			// New entities are only equal if the instance is the same
//			return super.equals(other);
//		}
    }
}
