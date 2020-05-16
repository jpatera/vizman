package eu.japtor.vizman.backend.entity;

import eu.japtor.vizman.ui.components.ArchIconBox;
import eu.japtor.vizman.ui.components.DigiIconBox;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
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
    @Column(name = "INVESTOR")
    private String investor;

    @Basic
    @Column(name = "OBJEDNATEL")
    private String objednatel;

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

////    @OneToMany(fetch = FetchType.LAZY)
////    @OneToMany(mappedBy = "kont", fetch = FetchType.EAGER, cascade = {CascadeType.PERSIST, CascadeType.MERGE} )
////    @OneToMany(mappedBy = "kont", fetch = FetchType.EAGER, cascade = CascadeType.REMOVE, orphanRemoval = false)
//    @OneToMany(mappedBy = "kont", fetch = FetchType.EAGER, cascade = CascadeType.REFRESH, orphanRemoval = false)
//    @OrderBy("czak DESC")
//    private List<Zak> zaks = new ArrayList<>();
////    @JoinTable(
////            name = "kont_zak",
////            joinColumns = @JoinColumn(
////                    name = "person_id", referencedColumnName = "id"),
////            inverseJoinColumns = @JoinColumn(
////                    name = "role_id", referencedColumnName = "id"))
//
//    @OneToMany(mappedBy = "kont", cascade = CascadeType.ALL, orphanRemoval = false)
//    @LazyCollection(LazyCollectionOption.FALSE)
//    private List<KontDoc> kontDocs = new ArrayList<>();
//
//
//    @ManyToOne(fetch = FetchType.EAGER)
//    @JoinColumn(name = "ID_KLIENT")
//    private Klient klient;

//    public Klient getKlient() {
//        return klient;
//    }
//    public void setKlient(Klient klient) {
//        this.klient = klient;
//    }

//    @Transient
//    private boolean checked;

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

    public String getInvestor() {
        return investor;
    }
    public void setInvestor(String investor) {
        this.investor = investor;
    }

    public String getObjednatel() {
        return objednatel;
    }
    public void setObjednatel(String objednatel) {
        this.objednatel = objednatel;
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
