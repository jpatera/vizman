package eu.japtor.vizman.backend.entity;

public enum TypeZak {
    KONT, ZAK, SUB, REZ, AKV, FAKT;

    public String getDisplayName() {
        return name();
    }
}
