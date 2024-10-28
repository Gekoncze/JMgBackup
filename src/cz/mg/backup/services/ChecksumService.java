package cz.mg.backup.services;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.backup.entities.Checksum;
import cz.mg.backup.entities.Directory;
import cz.mg.backup.entities.File;
import cz.mg.backup.entities.Settings;

public @Service class ChecksumService {
    private static volatile @Service ChecksumService instance;

    public static @Service ChecksumService getInstance() {
        if (instance == null) {
            synchronized (Service.class) {
                if (instance == null) {
                    instance = new ChecksumService();
                    instance.fileHashReader = FileHashReader.getInstance();
                }
            }
        }
        return instance;
    }

    private @Service FileHashReader fileHashReader;

    private ChecksumService() {
    }

    public void compute(@Mandatory Directory directory, @Mandatory Settings settings, boolean force) {
        for (File file : directory.getFiles()) {
            compute(file, settings, force);
        }

        for (Directory subdirectory : directory.getDirectories()) {
            compute(subdirectory, settings, force);
        }
    }

    public void compute(@Mandatory File file, @Mandatory Settings settings, boolean force) {
        if (force || file.getChecksum() == null) {
            Checksum checksum = new Checksum();
            checksum.setHash(fileHashReader.read(file.getPath(), settings));
            file.setChecksum(checksum);
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
