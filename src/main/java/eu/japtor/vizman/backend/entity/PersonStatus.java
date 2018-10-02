package eu.japtor.vizman.backend.entity;

public enum PersonStatus {
    NEW, ACTIVE, DISABLED, HIDDEN;

    public String getDisplayName() {
        return name();
    }
}
