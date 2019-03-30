package eu.japtor.vizman.backend.entity;

public enum PersonState {
    ACTIVE, PASSIVE, LOGGEDOUT;

    public String getDisplayName() {
        return name();
    }
}
