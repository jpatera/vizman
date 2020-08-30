package eu.japtor.vizman.backend.entity;

import eu.japtor.vizman.ui.components.ArchIconBox;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;


@Entity
@Table(name = "KONT_VIEW")
public class KontView extends AbstractGenIdEntity implements HasItemType, HasArchState, HasModifDates {

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
    @Column(name = "INVESTOR_NAME")
    private String investorName;

    @Basic
    @Column(name = "OBJEDNATEL_NAME")
    private String objednatelName;

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

    @Override
    public ItemType getTyp() {
        return typ;
    }
    public void setTyp(ItemType typ) {
        this.typ = typ;
    }

// ----------------------------------------------------------

    public static KontView getEmptyInstance() {
        KontView k = new KontView();
        k.setCkont(null);
        k.setRok(null);
        return k;
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

    public String getInvestorName() {
        return investorName;
    }
    public void setInvestorName(String investorName) {
        this.investorName = investorName;
    }

    public String getObjednatelName() {
        return objednatelName;
    }
    public void setObjednatelName(String objednatelName) {
        this.objednatelName = objednatelName;
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

    @Override
    public ArchIconBox.ArchState getArchState() {
        return ArchIconBox.ArchState.EMPTY;
//        if ((null == getZaks()) || (getZaks().size() == 0)) {
//            return ArchIconBox.ArchState.EMPTY;
//        } else if (getZaks().stream().allMatch(zak -> zak.getArch())) {
//            return ArchIconBox.ArchState.ARCHIVED;
//        } else {
//            return ArchIconBox.ArchState.ACTIVE;
//        }
    }

// ========================================

    @Override
    public int hashCode() {
		return uuid.hashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (!(other instanceof AbstractGenIdEntity)) return false;
        return getId() != null && getId().equals(((AbstractGenIdEntity) other).getId());
    }
}
