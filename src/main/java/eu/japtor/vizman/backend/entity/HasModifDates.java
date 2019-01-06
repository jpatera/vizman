package eu.japtor.vizman.backend.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

public interface HasModifDates {

    public LocalDate getDateCreate();
    public LocalDateTime getDatetimeUpdate();
}
