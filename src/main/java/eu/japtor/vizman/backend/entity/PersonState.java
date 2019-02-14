package eu.japtor.vizman.backend.entity;

public enum PersonState {
    NEW, ACTIVE, DISABLED, HIDDEN;

    public String getDisplayName() {
        return name();
    }
}
