package cz.mg.backup.services;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.requirement.Mandatory;

import java.nio.file.Path;

public @Service class PathService {
    private static volatile @Service PathService instance;

    public static @Service PathService getInstance() {
        if (instance == null) {
            synchronized (Service.class) {
                if (instance == null) {
                    instance = new PathService();
                }
            }
        }
        return instance;
    }

    private PathService() {
    }

    public @Mandatory Path removeLeadingPart(@Mandatory Path path) {
        Path modified = Path.of("");
        boolean first = true;
        for (Path part : path) {
            if (first) {
                first = false;
            } else {
                modified = modified.resolve(part);
            }
        }
        return modified;
    }
}
