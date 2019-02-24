package eu.japtor.vizman.backend.entity;

import javax.persistence.*;

@Entity
@Table(name = "CIN")
public class Cin extends AbstractGenIdEntity {

    final static String ATYP_ZACATEK_KONEC_CIN = "ZK";
    final static String ATYP_KONEC_CIN = "K";
    final static String ATYP_KONEC_DNE = "X";
    final static String ATYP_FIX_CAS = "F";
    final static String ATYP_AUTO = "A";

    private Integer poradi;
    private String akceTyp;
    private String akce;
    private String cinKod;
    private String cinnost;
    private Boolean calcprac;
    private String tmp;

    @Column(name = "PORADI")
    public Integer getPoradi() {
        return poradi;
    }

    public void setPoradi(Integer poradi) {
        this.poradi = poradi;
    }

    @Column(name = "AKCE_TYP")
    public String getAkceTyp() {
        return akceTyp;
    }

    public void setAkceTyp(String akceTyp) {
        this.akceTyp = akceTyp;
    }

    @Column(name = "AKCE")
    public String getAkce() {
        return akce;
    }

    public void setAkce(String akce) {
        this.akce = akce;
    }


    @Column(name = "CIN_KOD")
    public String getCinKod() {
        return cinKod;
    }

    public void setCinKod(String cinKod) {
        this.cinKod = cinKod;
    }


    @Column(name = "CINNOST")
    public String getCinnost() {
        return cinnost;
    }

    public void setCinnost(String cinnost) {
        this.cinnost = cinnost;
    }

    @Column(name = "CALCPRAC")
    public Boolean getCalcprac() {
        return calcprac;
    }

    public void setCalcprac(Boolean calcprac) {
        this.calcprac = calcprac;
    }

    @Column(name = "TMP")
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
