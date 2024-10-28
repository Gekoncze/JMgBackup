package cz.mg.backup.services;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.backup.entities.*;

public @Service class ChecksumService {
    private static volatile @Service ChecksumService instance;

    public static @Service ChecksumService getInstance() {
        if (instance == null) {
            synchronized (Service.class) {
                if (instance == null) {
                    instance = new ChecksumService();
                    instance.checksumReader = ChecksumReader.getInstance();
                }
            }
        }
        return instance;
    }

    private @Service ChecksumReader checksumReader;

    private ChecksumService() {
    }

    public void compute(@Mandatory Directory directory, @Mandatory Algorithm algorithm, boolean force) {
        for (File file : directory.getFiles()) {
            compute(file, algorithm, force);
        }

        for (Directory subdirectory : directory.getDirectories()) {
            compute(subdirectory, algorithm, force);
        }
    }

    public void compute(@Mandatory File file, @Mandatory Algorithm algorithm, boolean force) {
        if (force || file.getChecksum() == null) {
            file.setChecksum(checksumReader.read(file.getPath(), algorithm));
        }
    }

    public void clear(@Mandatory Directory directory) {
        for (File file : directory.getFiles()) {
            clear(file);
        }

        for (Directory subdirectory : directory.getDirectories()) {
            clear(subdirectory);
        }
    }

    public void clear(@Mandatory File file) {
        file.setChecksum(null);
    }
}
