package eu.japtor.vizman.backend.entity;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.Set;

@Entity
@Table(name = "KONTDOC")
public class KontDoc extends AbstractEntity {

    private String filename;
    private String note;
    private LocalDate dateCreate;
    private LocalDate dateUpdate;

    @Column(name = "FILENAME")
    public String getFilename() {
        return filename;
    }

    protected void setFilename(String filename) {
        this.filename = filename;
    }


    @Column(name = "NOTE")
    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }


    @Column(name = "DATE_CREATE")
    public LocalDate getDateCreate() {
        return dateCreate;
    }

    protected void setDateCreate(LocalDate dateCreate) {
        this.dateCreate = dateCreate;
    }

    @Column(name = "DATE_UPDATE")
    public LocalDate getDateUpdate() {
        return dateUpdate;
    }

    protected void setDateUpdate(LocalDate dateUpdate) {
        this.dateUpdate = dateUpdate;
    }


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_KONT")
    private Kont kont;
}
