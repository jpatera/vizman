package eu.japtor.vizman.backend.entity;

public enum ItemType {
    UNKNOWN, KONT, ZAK, SUB, REZ, AKV, FAKT;

    public String getDisplayName() {
        return name();
    }
}
