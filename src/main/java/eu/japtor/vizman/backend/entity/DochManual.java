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

    private LocalTime fromTimeOrig;

    private LocalTime toTime;

    private LocalTime toTimeOrig;

    private Cin.CinKod cinCinKod;

    private String cinnost;

    private String poznamka;

    private Cin.CinKod outsideCinKod;



    public DochManual(final LocalDate dochDate, final Cin cin
            , final LocalTime fromTime, final LocalTime toTime, final Cin.CinKod outsideCinKod) {

        this.dochDate = dochDate;
        this.cinCinKod = cin.getCinKod();
        this.cinnost = cin.getCinnost();
        this.fromTime = fromTime;
        this.fromTimeOrig = fromTime;
        this.toTime = toTime;
        this.toTimeOrig = toTime;
        this.outsideCinKod = outsideCinKod;
    }


    public LocalDate getDochDate() {
        return dochDate;
    }

    public void setDochDate(LocalDate dochDate) {
        this.dochDate = dochDate;
    }

    public LocalTime getFromTime() {
        return fromTime;
    }

    public void setFromTime(LocalTime fromTime) {
        this.fromTime = fromTime;
    }

    public LocalTime getFromTimeOrig() {
        return fromTimeOrig;
    }

    public void setFromTimeOrig(LocalTime fromTimeOrig) {
        this.fromTimeOrig = fromTimeOrig;
    }

    public LocalTime getToTime() {
        return toTime;
    }

    public void setToTime(LocalTime toTime) {
        this.toTime = toTime;
    }

    public LocalTime getToTimeOrig() {
        return toTimeOrig;
    }

    public void setToTimeOrig(LocalTime toTimeOrig) {
        this.toTimeOrig = toTimeOrig;
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

    public Cin.CinKod getOutsideCinKod() {
        return outsideCinKod;
    }

    public void setOutsideCinKod(Cin.CinKod outsideCinKod) {
        this.outsideCinKod = outsideCinKod;
    }
}
