package eu.japtor.vizman.backend.entity;

import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "PARAG")
public class Parag extends AbstractGenIdEntity {

    @Enumerated(EnumType.STRING)
    @Basic
    @Column(name = "TYP")
    private ItemType typ;


    @Column(name = "CPARAG")
    private String cparag;

    @Column(name = "TEXT")
    private String text;

    @Column(name = "DATE_CREATE")
    @CreationTimestamp
    private LocalDate dateCreate;

    @Column(name = "DATETIME_UPDATE")
    private LocalDateTime datetimeUpdate;


    public Parag() {
        super();
        this.typ = ItemType.PARAG;
    }


    public ItemType getTyp() {
        return typ;
    }
    public void setTyp(ItemType typ) {
        this.typ = typ;
    }

    public String getCparag() {
        return cparag;
    }
    public void setCparag(String cparag) {
        this.cparag = cparag;
    }

    public String getText() {
        return text;
    }
    public void setText(String text) {
        this.text = text;
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
}
