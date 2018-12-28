package eu.japtor.vizman.backend.entity;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.Set;

@Entity
@Table(name = "KONTDOC")
public class KontDoc extends AbstractEntity {

    private String filename;
    private String note;
    private LocalDate dateRegist;

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


    @Column(name = "DATE_REGIST")
    public LocalDate getDateRegist() {
        return dateRegist;
    }

    protected void setDateRegist(LocalDate dateRegist) {
        this.dateRegist = dateRegist;
    }


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_KONT")
    private Kont kont;
}
