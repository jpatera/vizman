package eu.japtor.vizman.backend.entity;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Entity
//@Table(name = "KONT", schema = "VIZMAN", catalog = "TEST")
//@Table(name = "KONT", schema = "VIZMAN")
@Table(name = "KONT")
@SequenceGenerator(initialValue = 1, name = "id_gen", sequenceName = "kont_seq")
public class Kont extends AbstractEntity {

    private String cisloKontraktu;
    private String text;
    private String firma;
    private Timestamp datumzad;
    private Boolean arch;

//    @Basic
//    @Column(name = "ID")
//    public long getId() {
//        return id;
//    }
//
//    public void setId(long id) {
//        this.id = id;
//    }

    @Basic
    @Column(name = "CISLO_KONTRAKTU")
    public String getCisloKontraktu() {
        return cisloKontraktu;
    }

    public void setCisloKontraktu(String cisloKontraktu) {
        this.cisloKontraktu = cisloKontraktu;
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
    @Column(name = "FIRMA")
    public String getFirma() {
        return firma;
    }

    public void setFirma(String firma) {
        this.firma = firma;
    }

    @Basic
    @Column(name = "DATUMZAD")
    public Timestamp getDatumzad() {
        return datumzad;
    }

    public void setDatumzad(Timestamp datumzad) {
        this.datumzad = datumzad;
    }

    @Basic
    @Column(name = "ARCH")
    public Boolean getArch() {
        return arch;
    }

    public void setArch(Boolean arch) {
        this.arch = arch;
    }

//    @Override
//    public boolean equals(Object o) {
//        if (this == o) return true;
//        if (o == null || getClass() != o.getClass()) return false;
//
//        Kont that = (Kont) o;
//
//        if (id != that.id) return false;
//        if (cisloKontraktu != null ? !cisloKontraktu.equals(that.cisloKontraktu) : that.cisloKontraktu != null)
//            return false;
//        if (text != null ? !text.equals(that.text) : that.text != null) return false;
//        if (firma != null ? !firma.equals(that.firma) : that.firma != null) return false;
//        if (datumzad != null ? !datumzad.equals(that.datumzad) : that.datumzad != null) return false;
//        if (arch != null ? !arch.equals(that.arch) : that.arch != null) return false;
//
//        return true;
//    }
//
//    @Override
//    public int hashCode() {
//        int result = (int) (id ^ (id >>> 32));
//        result = 31 * result + (cisloKontraktu != null ? cisloKontraktu.hashCode() : 0);
//        result = 31 * result + (text != null ? text.hashCode() : 0);
//        result = 31 * result + (firma != null ? firma.hashCode() : 0);
//        result = 31 * result + (datumzad != null ? datumzad.hashCode() : 0);
//        result = 31 * result + (arch != null ? arch.hashCode() : 0);
//        return result;
//    }


    @OneToMany(mappedBy = "kont")
    private List<Zak> zaks = new ArrayList<>();

    public void addZakentity(Zak zak) {
        this.zaks.add(zak);
        zak.setKont(this);
    }
}
