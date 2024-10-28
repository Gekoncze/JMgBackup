package cz.mg.backup.entities;

import cz.mg.annotations.classes.Entity;
import cz.mg.annotations.requirement.Required;
import cz.mg.annotations.storage.Value;

public @Entity class Checksum {
    private String hash;

    public Checksum() {
    }

    public Checksum(String hash) {
        this.hash = hash;
    }

    @Required @Value
    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }
}
