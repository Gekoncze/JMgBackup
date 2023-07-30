package cz.mg.backup.services;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.backup.entities.Directory;
import cz.mg.backup.entities.File;
import cz.mg.backup.exceptions.PropagatedException;

public @Service class DirectoryErrorService {
    private static volatile @Service DirectoryErrorService instance;

    public static @Service DirectoryErrorService getInstance() {
        if (instance == null) {
            synchronized (Service.class) {
                if (instance == null) {
                    instance = new DirectoryErrorService();
                }
            }
        }
        return instance;
    }

    private DirectoryErrorService() {
    }

    public void propagate(@Mandatory Directory directory) {
        directory.getErrors().removeIf(e -> e instanceof PropagatedException);

        Exception error = null;

        for (Directory child : directory.getDirectories()) {
            propagate(child);
            if (error == null && !child.getErrors().isEmpty()) {
                error = child.getErrors().getFirst();
            }
        }

        for (File child : directory.getFiles()) {
            if (error == null && !child.getErrors().isEmpty()) {
                error = child.getErrors().getFirst();
            }
        }

        if (error != null) {
            directory.getErrors().addLast(new PropagatedException(error));
        }
    }
}
