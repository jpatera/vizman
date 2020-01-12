package eu.japtor.vizman.backend.entity;

import eu.japtor.vizman.ui.components.ArchIconBox;
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
    @Column(name = "NAKL_MZDA")
    private BigDecimal naklMzda;

    @Basic
    @Column(name = "NAKL_MZDA_P8")
    private BigDecimal naklMzdaP8;

    // TODO : to be removed, from DB too
    @Basic
    @Column(name = "NAKL_POJIST")
    private BigDecimal naklPojist;

    // TODO : to be removed, from DB too
    @Basic
    @Column(name = "NAKL_POJIST_P8")
    private BigDecimal naklPojistP8;

    @Basic
    @Column(name = "RP")
    private BigDecimal rp;  // Rozpracovanost posledni platna

    @Basic
    @Column(name = "RM4")
    private BigDecimal rm4;  // Rozpracovanost na konci Q4 (minus 4) predminuleho roku

    @Basic
    @Column(name = "RM3")
    private BigDecimal rm3;  // Rozpracovanost na konci Q1 (minus 3) minuleho roku

    @Basic
    @Column(name = "RM2")
    private BigDecimal rm2;  // Rozpracovanost na konci Q2 (minus 2) minuleho roku

    @Basic
    @Column(name = "RM1")
    private BigDecimal rm1;  // Rozpracovanost na konci Q3 (minus 1) predminuleho roku

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

    @Basic
    @Column(name = "KURZ_EUR")
    private BigDecimal kurzEur;

    @Basic
    @Column(name = "RX_RY_VYKON")
    private BigDecimal rxRyVykon;

    @Basic
    @Column(name = "VYSLEDEK_BY_KURZ")
    private BigDecimal vysledekByKurz;

    @Basic
    @Column(name = "VYSLEDEK_P8_BY_KURZ")
    private BigDecimal vysledekP8ByKurz;

    @Transient
    private boolean checked;

    //    @OneToMany(mappedBy = "zakr", fetch = FetchType.LAZY, cascade = CascadeType.REFRESH, orphanRemoval = false)
    @OneToMany(mappedBy = "zakr", cascade = CascadeType.REFRESH, orphanRemoval = false)
    @OrderBy("rok DESC, qa DESC")
    private List<Zaqa> zaqas = new ArrayList<>();

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

    public BigDecimal getNaklMzda() {
        return naklMzda;
    }
    public void setNaklMzda(BigDecimal naklMzda) {
        this.naklMzda = naklMzda;
    }

//    public BigDecimal getNaklPojist() {
//        return naklPojist;
//    }
//    public void setNaklPojist(BigDecimal naklPojist) {
//        this.naklPojist = naklPojist;
//    }

    public BigDecimal getNaklMzdaP8() {
        return naklMzdaP8;
    }
    public void setNaklMzdaP8(BigDecimal naklMzdaP8) {
        this.naklMzdaP8 = naklMzdaP8;
    }

//    public BigDecimal getNaklPojistP8() {
//        return naklPojistP8;
//    }
//    public void setNaklPojistP8(BigDecimal naklPojistP8) {
//        this.naklPojistP8 = naklPojistP8;
//    }

    public BigDecimal getRp() {
        return rp;
    }
    public void setRp(BigDecimal rp) {
        this.rp = rp;
    }

    public BigDecimal getRm4() {
        return rm4;
    }
    public void setRm4(BigDecimal rm4) {
        this.rm4 = rm4;
    }

    public BigDecimal getRm3() {
        return rm3;
    }
    public void setRm3(BigDecimal rm3) {
        this.rm3 = rm3;
    }

    public BigDecimal getRm2() {
        return rm2;
    }
    public void setRm2(BigDecimal rm2) {
        this.rm2 = rm2;
    }

    public BigDecimal getRm1() {
        return rm1;
    }
    public void setRm1(BigDecimal rm1) {
        this.rm1 = rm1;
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

    public BigDecimal getKurzEur() {
        return kurzEur;
    }
    public void setKurzEur(BigDecimal kurzEur) {
        this.kurzEur = kurzEur;
    }

    public BigDecimal getRxRyVykon() {
        return rxRyVykon;
    }
    public void setRxRyVykon(BigDecimal rxRyVykon) {
        this.rxRyVykon = rxRyVykon;
//        this.rxRyVykon = calcRxRyVykon();
    }

    public BigDecimal getVysledekByKurz() {
        return vysledekByKurz;
    }
    public void setVysledekByKurz(BigDecimal vysledekByKurz) {
        this.vysledekByKurz = vysledekByKurz;
    }

    public BigDecimal getVysledekP8ByKurz() {
        return vysledekP8ByKurz;
    }
    public void setVysledekP8ByKurz(BigDecimal vysledekP8ByKurz) {
        this.vysledekP8ByKurz = vysledekP8ByKurz;
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


// =============================================


//    @Transient
//    private BigDecimal rpHotovo;

//    @Transient
    public BigDecimal getRpHotovo() {
        if (null == rp || null == honorCisty) {
            return null;
        } else {
            return rp.multiply(honorCisty).divide(BigDecimal.valueOf(100L));
        }
    }

//    @Transient
//    private BigDecimal rpZbyva;

//    @Transient
    public BigDecimal getRpZbyva() {
        if (null == rp || null == honorCisty) {
            return null;
        } else {
            return honorCisty.subtract(rp.multiply(honorCisty).divide(BigDecimal.valueOf(100L)));
        }
    }


//    @Transient
//    private BigDecimal rpHotovoByKurz;

//    @Transient
//    public static BigDecimal calcRpHotovoByKurz(BigDecimal rpHotovo, BigDecimal kurzEur) {
//        if (null == rpHotovo) {
//            return null;
//        } else {
//            return getMena() == Mena.EUR ?
//                    rpHotovo.multiply(kurzEur) :
//                    rpHotovo;
//        }
//    }

//    @Transient
//    BigDecimal rpHotovoByKurz;

    @Transient
    public BigDecimal getHonorCistyByKurz() {
//        BigDecimal honorCisty = getHonorCisty();
        return (null == honorCisty) ? null : honorCisty.multiply(kurzEur);
    }

    @Transient
    public BigDecimal getRpHotovoByKurz() {
        BigDecimal rpHotovo = getRpHotovo();
        return (null == rpHotovo) ? null : rpHotovo.multiply(kurzEur);
    }

    @Transient
    public BigDecimal getRpZbyvaByKurz() {
        BigDecimal rpZbyva = getRpZbyva();
        return (null == rpZbyva) ? null : rpZbyva.multiply(kurzEur);
    }

    @Transient
    private BigDecimal rxRyVykonByKurz;

    @Transient
    public BigDecimal getRxRyVykonByKurz() {
        return (null == rxRyVykon) ? null : rxRyVykon.multiply(kurzEur);
    }

    @Transient
    public BigDecimal calcRxRyVykon(String rxParam, String ryParam) {
        BigDecimal vykRx = BigDecimal.ZERO;
        BigDecimal vykRy = BigDecimal.ZERO;

        if (rxParam == null) {
            return vykRx;
        }
        switch (rxParam) {
            case "R-4":
                if (null != rm4 && null != honorCisty) {
                    vykRx = rm4.multiply(honorCisty).divide(BigDecimal.valueOf(100L));
                }
                break;
            case "R-3":
                if (null != rm3 && null != honorCisty) {
                    vykRx = rm3.multiply(honorCisty).divide(BigDecimal.valueOf(100L));
                }
                break;
            case "R-2":
                if (null != rm2 && null != honorCisty) {
                    vykRx = rm2.multiply(honorCisty).divide(BigDecimal.valueOf(100L));
                }
                break;
            case "R-1":
                if (null != rm1 && null != honorCisty) {
                    vykRx = rm1.multiply(honorCisty).divide(BigDecimal.valueOf(100L));
                }
                break;
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
            case "R-4":
                if (null != rm4 && null != honorCisty) {
                    vykRy = rm4.multiply(honorCisty).divide(BigDecimal.valueOf(100L));
                }
                break;
            case "R-3":
                if (null != rm3 && null != honorCisty) {
                    vykRy = rm3.multiply(honorCisty).divide(BigDecimal.valueOf(100L));
                }
                break;
            case "R-2":
                if (null != rm2 && null != honorCisty) {
                    vykRy = rm2.multiply(honorCisty).divide(BigDecimal.valueOf(100L));
                }
                break;
            case "R-1":
                if (null != rm1 && null != honorCisty) {
                    vykRy = rm1.multiply(honorCisty).divide(BigDecimal.valueOf(100L));
                }
                break;
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
//        rxRyVykon = vykRy.subtract(vykRx);
    }

//    @Transient
    public BigDecimal calcVysledekByKurz(BigDecimal koefPojist, BigDecimal koefRezie) {
//        BigDecimal honCalc= getHonorCistyByKurz();
        BigDecimal hotovoCalc = getRpHotovoByKurz();
        if (null == hotovoCalc) {
            hotovoCalc = BigDecimal.ZERO;
        }
        BigDecimal mzdaCalc = (null == naklMzda) ? BigDecimal.ZERO : naklMzda;

        return hotovoCalc.subtract(
                mzdaCalc.multiply(koefPojist.add(BigDecimal.ONE)).multiply(koefRezie.add(BigDecimal.ONE))
        );
    }

    public BigDecimal calcVysledekP8ByKurz(BigDecimal koefPojist, BigDecimal koefRezie) {
        BigDecimal hotovoCalc = getRpHotovoByKurz();
        if (null == hotovoCalc) {
            hotovoCalc = BigDecimal.ZERO;
        }
        BigDecimal mzdaP8Calc = (null == naklMzdaP8) ? BigDecimal.ZERO : naklMzdaP8;

        return hotovoCalc.subtract(
                mzdaP8Calc.multiply(koefPojist.add(BigDecimal.ONE)).multiply(koefRezie.add(BigDecimal.ONE))
        );
    }

    public BigDecimal calcNaklMzdyPojistRezie(BigDecimal koefPojist, BigDecimal koefRezie) {
        if (null == naklMzda) {
            return null;
        } else {
            return naklMzda.multiply(koefPojist.add(BigDecimal.ONE)).multiply(koefRezie.add(BigDecimal.ONE));
        }
    }

    public BigDecimal calcNaklMzdyP8PojistRezie(BigDecimal koefPojist, BigDecimal koefRezie) {
        if (null == naklMzdaP8) {
            return null;
        } else {
            return naklMzdaP8.multiply(koefPojist.add(BigDecimal.ONE)).multiply(koefRezie.add(BigDecimal.ONE));
        }
    }

    @Transient
    public boolean isChecked() {
        return checked;
    }

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
    private String kzText;

    @Transient
    public String getCkzTextFull() {
        StringBuilder builder = new StringBuilder();
        builder .append(getCkont())
                .append("/")
                .append(getCzak())
                .append(" : ")
                .append(getKzTextFull())
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

    @Transient
    public String getKzTextFull() {
        StringBuilder builder = new StringBuilder();
        builder .append(getTextKontNotNull())
                .append(" / ")
                .append(getTextZakNotNull())
        ;
        return builder.toString();
    }


    @Transient
    private String kzTextShort;

    @Transient
    public String getKzTextShort() {
        StringBuilder builder = new StringBuilder();
        builder .append(StringUtils.substring(getTextKontNotNull(), 0, 15))
                .append(" / ")
                .append(StringUtils.substring(getTextZakNotNull(), 0, 15))
        ;
        return builder.toString();
    }

    @Override
    public int hashCode() {
        return 31;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (!(other instanceof AbstractGenIdEntity)) return false;
        return id != null && id.equals(((Zakr) other).id);
    }
}
