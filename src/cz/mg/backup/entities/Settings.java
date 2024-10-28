package cz.mg.backup.entities;

import cz.mg.annotations.classes.Entity;
import cz.mg.annotations.requirement.Required;
import cz.mg.annotations.storage.Value;

public @Entity class Settings {
    private Algorithm algorithm;

    public Settings() {
    }

    public Settings(Algorithm algorithm) {
        this.algorithm = algorithm;
    }

    @Required @Value
    public Algorithm getAlgorithm() {
        return algorithm;
    }

    public void setAlgorithm(Algorithm algorithm) {
        this.algorithm = algorithm;
    }
}
