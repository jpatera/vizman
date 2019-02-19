package eu.japtor.vizman.backend.entity;

import javax.persistence.*;

@Entity
@Table(name = "CIN")
public class Cin extends AbstractGenIdEntity {

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
}
