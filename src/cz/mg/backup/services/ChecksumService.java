package cz.mg.backup.services;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.backup.components.Progress;
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

    public void compute(@Mandatory Node node, @Mandatory Algorithm algorithm, @Mandatory Progress progress) {
        if (node instanceof File file) {
            compute(file, algorithm, progress);
        } else if (node instanceof Directory directory) {
            compute(directory, algorithm, progress);
        } else {
            throw new UnsupportedOperationException(
                "Unsupported node of type " + node.getClass().getSimpleName() + "."
            );
        }
    }

    public void compute(@Mandatory Directory directory, @Mandatory Algorithm algorithm, @Mandatory Progress progress) {
        if (progress.getLimit() < 1) {
            progress.setLimit(directory.getProperties().getTotalCount());
        }

        for (File file : directory.getFiles()) {
            compute(file, algorithm, progress.nest("Checksum " + file.getPath().getFileName()));
            progress.step();
        }

        for (Directory subdirectory : directory.getDirectories()) {
            compute(subdirectory, algorithm, progress);
            progress.step();
        }
    }

    public void compute(@Mandatory File file, @Mandatory Algorithm algorithm, @Mandatory Progress progress) {
        if (file.getChecksum() == null) {
            file.setChecksum(checksumReader.read(file.getPath(), algorithm, progress));
        }
    }

    public void clear(@Mandatory Node node) { // TODO - track progress
        if (node instanceof File file) {
            clear(file);
        } else if (node instanceof Directory directory) {
            clear(directory);
        } else {
            throw new UnsupportedOperationException(
                "Unsupported node of type " + node.getClass().getSimpleName() + "."
            );
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
