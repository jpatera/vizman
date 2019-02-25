package eu.japtor.vizman.backend.entity;

import javax.persistence.*;

@Entity
@Table(name = "CIN")
public class Cin extends AbstractGenIdEntity {

    public static final String ATYP_ZACATEK_KONEC_CIN = "ZK";
    public static final String ATYP_KONEC_CIN = "K";
    public static final String ATYP_KONEC_DNE = "X";
    public static final String ATYP_FIX_CAS = "F";
    public static final String ATYP_AUTO = "A";

    @Column(name = "PORADI")
    private Integer poradi;

    @Column(name = "AKCE_TYP")
    private String akceTyp;

    @Column(name = "AKCE")
    private String akce;

    @Column(name = "CIN_KOD")
    @Enumerated(EnumType.STRING)
    @Basic
    private CinKod cinKod;

    @Column(name = "CINNOST")
    private String cinnost;

    @Column(name = "CALCPRAC")
    private Boolean calcprac;

    @Column(name = "TMP")
    private String tmp;

    public Integer getPoradi() {
        return poradi;
    }

    public void setPoradi(Integer poradi) {
        this.poradi = poradi;
    }

    public String getAkceTyp() {
        return akceTyp;
    }

    public void setAkceTyp(String akceTyp) {
        this.akceTyp = akceTyp;
    }

    public String getAkce() {
        return akce;
    }

    public void setAkce(String akce) {
        this.akce = akce;
    }


    public CinKod getCinKod() {
        return cinKod;
    }

    public void setCinKod(CinKod cinKod) {
        this.cinKod = cinKod;
    }


    public String getCinnost() {
        return cinnost;
    }

    public void setCinnost(String cinnost) {
        this.cinnost = cinnost;
    }

    public Boolean getCalcprac() {
        return calcprac;
    }

    public void setCalcprac(Boolean calcprac) {
        this.calcprac = calcprac;
    }

    public String getTmp() {
        return tmp;
    }

    public void setTmp(String tmp) {
        this.tmp = tmp;
    }


    public enum CinKod {
        P,      // Prichod,         Na pracovisti
        MO,     // Odchod na obed   Mimo pracoviste - obed
        PM,     // Odchod pracovne  Pracovne mimo
        L,      // Odchod k lekari  U lekare

        KP,     // Ukonceni/preruseni prace
        XD,     // Konec dne

        N,      // Nahrada                  Nahrada (nepouziva se)
        ne,     // Nemoc, OCR (cely den)    Nemoc, OCR
        dc,     // Dovolena (cely den)      Dovolena
        dp,     // Dovolena (1/2 dne)       Dovolena
        nv,     // Neplacene volno          Neplac. volno
        OA,     // Obed automaticky         Obed autom.
        OO;     // Obed opravgeny           Obed oprav (asi se nepouziva)
    }
}
