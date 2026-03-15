package cz.mg.backup.services.detector;

import cz.mg.annotations.classes.Component;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.annotations.requirement.Optional;
import cz.mg.backup.entities.Algorithm;
import cz.mg.backup.entities.File;

public @Component class Converter {
    private final boolean compareName;
    private final boolean compareType;
    private final boolean compareSize;
    private final boolean compareHash;

    public Converter(
        boolean compareName,
        boolean compareType,
        boolean compareSize,
        boolean compareHash
    ) {
        this.compareName = compareName;
        this.compareType = compareType;
        this.compareSize = compareSize;
        this.compareHash = compareHash;
    }

    public @Mandatory Key convert(@Mandatory File file) {
        return new Key(
            getName(file),
            getSize(file),
            getAlgorithm(file),
            getHash(file)
        );
    }

    private @Optional String getName(@Mandatory File file) {
        return splitName(file.getPath().getFileName().toString());
    }

    private @Optional Long getSize(@Mandatory File file) {
        return compareSize
            ? file.getProperties().getSize()
            : null;
    }

    private @Optional Algorithm getAlgorithm(@Mandatory File file) {
        return compareHash && file.getChecksum() != null
            ? file.getChecksum().getAlgorithm()
            : null;
    }

    private @Optional String getHash(@Mandatory File file) {
        return compareHash && file.getChecksum() != null
            ? file.getChecksum().getHash()
            : null;
    }

    private @Optional String splitName(@Mandatory String name) {
        if (compareName && compareType) {
            return name;
        } else if (compareName) {
            int p = name.lastIndexOf('.');
            return p > 0 ? name.substring(0, p) : name;
        } else if (compareType) {
            int p = name.lastIndexOf('.');
            return p > 0 && (p + 1) < name.length() ? name.substring(p + 1) : null;
        } else {
            return null;
        }
    }
}
