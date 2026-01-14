package cz.mg.backup.services;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.annotations.requirement.Optional;
import cz.mg.backup.components.Progress;
import cz.mg.backup.entities.*;
import cz.mg.collections.map.Map;
import cz.mg.collections.pair.Pair;

import java.nio.file.Path;
import java.util.Date;
import java.util.Objects;

public @Service class ChecksumManager {
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

    public void compute(@Mandatory File file, @Mandatory Algorithm algorithm, @Mandatory Progress progress) {
        if (file.getChecksum() == null || file.getChecksum().getAlgorithm() != algorithm) {
            file.setChecksum(checksumReader.read(
                file.getPath(),
                algorithm,
                progress.nest("Checksum " + file.getPath().getFileName())
            ));
        }
    }

    public void clear(@Mandatory File file) {
        file.setChecksum(null);
    }

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
            progress
        );
        return checksums;
    }

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
            progress
        );
    }
}
