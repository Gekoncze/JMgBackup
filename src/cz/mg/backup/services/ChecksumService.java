package cz.mg.backup.services;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.annotations.requirement.Optional;
import cz.mg.backup.components.Progress;
import cz.mg.backup.entities.*;
import cz.mg.collections.list.List;
import cz.mg.collections.map.Map;
import cz.mg.collections.pair.Pair;

import java.nio.file.Path;
import java.util.Date;
import java.util.Objects;

public @Service class ChecksumService {
    private static volatile @Service ChecksumService instance;

    public static @Service ChecksumService getInstance() {
        if (instance == null) {
            synchronized (Service.class) {
                if (instance == null) {
                    instance = new ChecksumService();
                    instance.checksumReader = ChecksumReader.getInstance();
                    instance.directoryService = DirectoryService.getInstance();
                }
            }
        }
        return instance;
    }

    private @Service ChecksumReader checksumReader;
    private @Service DirectoryService directoryService;

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
        if (file.getChecksum() == null || file.getChecksum().getAlgorithm() != algorithm) {
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

    public @Mandatory Map<Path, Pair<Checksum, Date>> collect(
        @Optional Directory directory,
        @Mandatory Progress progress
    ) {
        progress.setLimit(estimate(directory));

        Map<Path, Pair<Checksum, Date>> checksums = new Map<>();
        directoryService.forEachFile(
            directory,
            file -> checksums.set(
                file.getPath(),
                new Pair<>(file.getChecksum(), file.getProperties().getModified())
            )
        );
        return checksums;
    }

    public void restore(
        @Optional Directory directory,
        @Mandatory Map<Path, Pair<Checksum, Date>> map,
        @Mandatory Progress progress
    ) {
        progress.setLimit(estimate(directory));

        directoryService.forEachFile(
            directory,
            file -> {
                Pair<Checksum, Date> pair = map.getOptional(file.getPath());
                if (pair != null) {
                    if (Objects.equals(file.getProperties().getModified(), pair.getValue())) {
                        file.setChecksum(pair.getKey());
                    }
                }
            }
        );
    }

    private long estimate(@Mandatory List<Node> nodes) {
        long estimate = 0;
        for (Node node : nodes) {
            estimate += estimate(node);
        }
        return estimate;
    }

    private long estimate(@Optional Node node) {
        if (node instanceof Directory directory) {
            return directory.getProperties().getTotalCount() + 1;
        } else if (node != null) {
            return 1;
        } else {
            return 0;
        }
    }
}
