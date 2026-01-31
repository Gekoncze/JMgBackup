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

public @Service class ChecksumManager {
    private static final String COMPUTE_DESCRIPTION = "Compute checksums";
    private static final String CLEAR_DESCRIPTION = "Clear checksums";
    private static final String COLLECT_DESCRIPTION = "Collect checksums";
    private static final String RESTORE_DESCRIPTION = "Restore checksums";

    private static volatile @Service ChecksumManager instance;

    public static @Service ChecksumManager getInstance() {
        if (instance == null) {
            synchronized (Service.class) {
                if (instance == null) {
                    instance = new ChecksumManager();
                    instance.checksumReader = ChecksumReader.getInstance();
                    instance.treeIterator = TreeIterator.getInstance();
                }
            }
        }
        return instance;
    }

    private @Service ChecksumReader checksumReader;
    private @Service TreeIterator treeIterator;

    private ChecksumManager() {
    }

    /**
     * Computes checksum for given trees.
     * For each file, checksum is computed if missing or using different algorithm.
     */
    public void compute(@Mandatory List<Node> nodes, @Mandatory Algorithm algorithm, @Mandatory Progress progress) {
        treeIterator.forEachFile(
            nodes,
            file -> {
                if (file.getChecksum() == null || file.getChecksum().getAlgorithm() != algorithm) {
                    file.setChecksum(checksumReader.read(file.getPath(), algorithm, progress.nest()));
                    progress.unnest();
                }
            },
            progress,
            COMPUTE_DESCRIPTION
        );
    }

    /**
     * Clears checksum for given trees.
     */
    public void clear(@Mandatory List<Node> nodes, @Mandatory Progress progress) {
        treeIterator.forEachFile(
            nodes,
            file -> file.setChecksum(null),
            progress,
            CLEAR_DESCRIPTION
        );
    }

    /**
     * Collects checksums from given tree.
     * Checksum is stored for each file path together with last modification date.
     */
    public @Mandatory Map<Path, Pair<Checksum, Date>> collect(
        @Optional Directory directory,
        @Mandatory Progress progress
    ) {
        Map<Path, Pair<Checksum, Date>> checksums = new Map<>();
        treeIterator.forEachFile(
            directory,
            file -> checksums.set(
                file.getPath(),
                new Pair<>(file.getChecksum(), file.getProperties().getModified())
            ),
            progress,
            COLLECT_DESCRIPTION
        );
        return checksums;
    }

    /**
     * Restores checksums for given tree.
     * Only matching values are restored, rest is ignored.
     */
    public void restore(
        @Optional Directory directory,
        @Mandatory Map<Path, Pair<Checksum, Date>> map,
        @Mandatory Progress progress
    ) {
        treeIterator.forEachFile(
            directory,
            file -> {
                Pair<Checksum, Date> pair = map.getOptional(file.getPath());
                if (pair != null) {
                    if (Objects.equals(file.getProperties().getModified(), pair.getValue())) {
                        file.setChecksum(pair.getKey());
                    }
                }
            },
            progress,
            RESTORE_DESCRIPTION
        );
    }
}
