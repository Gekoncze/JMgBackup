package cz.mg.backup.services.detector;

import cz.mg.annotations.classes.Component;
import cz.mg.annotations.requirement.Optional;
import cz.mg.backup.entities.Algorithm;

public @Component class Key {
    private final @Optional String name;
    private final @Optional Long size;
    private final @Optional Algorithm algorithm;
    private final @Optional String hash;

    public Key(
        @Optional String name,
        @Optional Long size,
        @Optional Algorithm algorithm,
        @Optional String hash
    ) {
        this.name = name;
        this.size = size;
        this.algorithm = algorithm;
        this.hash = hash;
    }

    public @Optional String getName() {
        return name;
    }

    public @Optional Long getSize() {
        return size;
    }

    public @Optional Algorithm getAlgorithm() {
        return algorithm;
    }

    public @Optional String getHash() {
        return hash;
    }
}
