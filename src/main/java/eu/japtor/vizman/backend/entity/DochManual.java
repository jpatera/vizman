package eu.japtor.vizman.backend.entity;

import javax.persistence.*;
import java.io.Serializable;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class DochManual implements Serializable {

    private LocalDate dochDate;

    private String dochState;

    private LocalTime fromTime;

    private LocalTime toTime;

    private Cin.CinKod cinCinKod;

    private String cinnost;

    private String poznamka;


    public DochManual(final LocalDate dochDate, final Cin.CinKod cinCinKod, final String cinnost
            , final LocalTime fromTime, final LocalTime toTime) {

        this.dochDate = dochDate;
        this.cinCinKod = cinCinKod;
        this.cinnost = cinnost;
        this.fromTime = fromTime;
        this.toTime = toTime;
    }


    public LocalDate getdDochDate() {
        return dochDate;
    }

    public void setdDochDate(LocalDate dochDate) {
        this.dochDate = dochDate;
    }

    public LocalTime getFromTime() {
        return fromTime;
    }

    public void setFromTime(LocalTime fromTime) {
        this.fromTime = fromTime;
    }

    public LocalTime getToTime() {
        return toTime;
    }

    public void setToTime(LocalTime toTime) {
        this.toTime = toTime;
    }

    public String getDochState() {
        return dochState;
    }

    public void setDochState(String dochState) {
        this.dochState = dochState;
    }

    public Cin.CinKod getCinCinKod() {
        return cinCinKod;
    }

    public void setCinCinKod(Cin.CinKod cinCinKod) {
        this.cinCinKod = cinCinKod;
    }

    public String getCinnost() {
        return cinnost;
    }

    public void setCinnost(String cinnost) {
        this.cinnost = cinnost;
    }

    public String getPoznamka() {
        return poznamka;
    }

    public void setPoznamka(String poznamka) {
        this.poznamka = poznamka;
    }
}
