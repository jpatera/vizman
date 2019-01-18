package eu.japtor.vizman.backend.entity;

import javax.persistence.*;

@Entity
@Table(name = "CIN")
public class Cin extends AbstractGenIdEntity {

    private Integer poradi;
    private String cinT1;
    private String cinT2;
    private String akce;
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

    @Column(name = "CIN_T1")
    public String getCinT1() {
        return cinT1;
    }

    public void setCinT1(String cinT1) {
        this.cinT1 = cinT1;
    }

    @Column(name = "CIN_T2")
    public String getCinT2() {
        return cinT2;
    }

    public void setCinT2(String cinT2) {
        this.cinT2 = cinT2;
    }

    @Column(name = "AKCE")
    public String getAkce() {
        return akce;
    }

    public void setAkce(String akce) {
        this.akce = akce;
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
