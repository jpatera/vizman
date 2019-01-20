package eu.japtor.vizman.backend.entity;

import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "ZAKDOC")
public class ZakDoc extends AbstractGenIdEntity {

    @Column(name = "FILENAME")
    private String filename;

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


    public String getFilename() {
        return filename;
    }

    protected void setFilename(String filename) {
        this.filename = filename;
    }


    public String getNote() {
        return note;
    }

    protected void setNote(String note) {
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
