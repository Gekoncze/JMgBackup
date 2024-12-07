package cz.mg.backup.services;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.backup.components.Progress;
import cz.mg.backup.entities.*;
import cz.mg.collections.list.List;

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

    public void compute(@Mandatory List<Node> nodes, @Mandatory Algorithm algorithm, @Mandatory Progress progress) {
        progress.setLimit(estimate(nodes));

        for (Node node : nodes) {
            if (node instanceof File file) {
                compute(file, algorithm, progress);
                progress.step();
            } else if (node instanceof Directory directory) {
                compute(directory, algorithm, progress);
                progress.step();
            } else {
                throw new UnsupportedOperationException(
                    "Unsupported node of type " + node.getClass().getSimpleName() + "."
                );
            }
        }
    }

    private void compute(@Mandatory Directory directory, @Mandatory Algorithm algorithm, @Mandatory Progress progress) {
        for (File file : directory.getFiles()) {
            compute(file, algorithm, progress);
            progress.step();
        }

        for (Directory subdirectory : directory.getDirectories()) {
            compute(subdirectory, algorithm, progress);
            progress.step();
        }
    }

    private void compute(@Mandatory File file, @Mandatory Algorithm algorithm, @Mandatory Progress progress) {
        if (file.getChecksum() == null) {
            file.setChecksum(checksumReader.read(
                file.getPath(),
                algorithm,
                progress.nest("Checksum " + file.getPath().getFileName())
            ));
        }
    }

    public void clear(@Mandatory List<Node> nodes, @Mandatory Progress progress) {
        progress.setLimit(estimate(nodes));

        for (Node node : nodes) {
            if (node instanceof File file) {
                clear(file);
                progress.step();
            } else if (node instanceof Directory directory) {
                clear(directory, progress);
                progress.step();
            } else {
                throw new UnsupportedOperationException(
                    "Unsupported node of type " + node.getClass().getSimpleName() + "."
                );
            }
        }
    }

    private void clear(@Mandatory Directory directory, @Mandatory Progress progress) {
        for (File file : directory.getFiles()) {
            clear(file);
            progress.step();
        }

        for (Directory subdirectory : directory.getDirectories()) {
            clear(subdirectory, progress);
            progress.step();
        }
    }

    private void clear(@Mandatory File file) {
        file.setChecksum(null);
    }

    private long estimate(@Mandatory List<Node> nodes) {
        long estimate = 0;

        for (Node node : nodes) {
            if (node instanceof Directory directory) {
                estimate += directory.getProperties().getTotalCount();
            }

            estimate++;
        }

        return estimate;
    }
}
