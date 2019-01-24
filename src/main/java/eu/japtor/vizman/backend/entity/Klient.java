package eu.japtor.vizman.backend.entity;

import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "KLIENT")
public class Klient extends AbstractGenIdEntity {

    @Enumerated(EnumType.STRING)
    @Basic
    @Column(name = "TYP")
    private ItemType typ;


    @Column(name = "NAME")
    private String name;

    @Column(name = "NOTE")
    private String note;

    @Column(name = "DATE_CREATE")
    @CreationTimestamp
    private LocalDate dateCreate;

    @Column(name = "DATETIME_UPDATE")
    private LocalDateTime datetimeUpdate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_ZAK")
    private Zak zak;

    public Klient() {
        super();
        this.typ = ItemType.KLI;
    }


    public ItemType getTyp() {
        return typ;
    }

    public void setTyp(ItemType typ) {
        this.typ = typ;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }


    public LocalDate getDateCreate() {
        return dateCreate;
    }

    protected void setDateCreate(LocalDate dateCreate) {
        this.dateCreate = dateCreate;
    }


    public LocalDateTime getDatetimeUpdate() {
        return datetimeUpdate;
    }

    protected void setDatetimeUpdate(LocalDateTime datetimeUpdate) {
        this.datetimeUpdate = datetimeUpdate;
    }


    public Zak getZak() {
        return zak;
    }

    public void setZak(Zak zak) {
        this.zak = zak;
    }
}
