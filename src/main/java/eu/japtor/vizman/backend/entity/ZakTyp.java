package eu.japtor.vizman.backend.entity;

public enum ZakTyp {
    KONT, ZAK, SUB, FAKT;

    public String getDisplayName() {
        return name();
    }
}
