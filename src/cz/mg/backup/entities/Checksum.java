package cz.mg.backup.entities;

import cz.mg.annotations.classes.Entity;
import cz.mg.annotations.requirement.Required;
import cz.mg.annotations.storage.Value;

public @Entity class Checksum {
    private Algorithm algorithm;
    private String hash;

    public Checksum() {
    }

    public Checksum(Algorithm algorithm, String hash) {
        this.algorithm = algorithm;
        this.hash = hash;
    }

    @Required @Value
    public Algorithm getAlgorithm() {
        return algorithm;
    }

    public void setAlgorithm(Algorithm algorithm) {
        this.algorithm = algorithm;
    }

    @Required @Value
    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }
}
