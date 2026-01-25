package cz.mg.backup.test;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.annotations.requirement.Optional;
import cz.mg.backup.components.Progress;
import cz.mg.backup.entities.Algorithm;
import cz.mg.backup.entities.Checksum;
import cz.mg.backup.entities.Directory;
import cz.mg.backup.entities.File;
import cz.mg.backup.entities.FileProperties;
import cz.mg.backup.entities.Node;
import cz.mg.backup.services.StatisticsCounter;
import cz.mg.collections.list.List;

import java.nio.file.Path;

public @Service class TestFactory {
    private static volatile @Service TestFactory instance;

    public static @Service TestFactory getInstance() {
        if (instance == null) {
            synchronized (Service.class) {
                if (instance == null) {
                    instance = new TestFactory();
                    instance.statisticsCounter = StatisticsCounter.getInstance();
                }
            }
        }
        return instance;
    }

    private @Service StatisticsCounter statisticsCounter;

    private TestFactory() {
    }

    public @Mandatory File file(@Mandatory String name) {
        return file(Path.of(name));
    }

    public @Mandatory File file(@Mandatory Path path) {
        File file = new File();
        file.setPath(path);
        return file;
    }

    public @Mandatory File file(
        @Mandatory String name,
        @Optional FileProperties properties,
        @Optional Checksum checksum
    ) {
        return file(Path.of(name), properties, checksum);
    }

    public @Mandatory File file(
        @Mandatory Path path,
        @Optional FileProperties properties,
        @Optional Checksum checksum
    ) {
        File file = new File();
        file.setPath(path);
        file.setProperties(properties != null ? properties : new FileProperties());
        file.setChecksum(checksum);
        return file;
    }

    public @Mandatory Directory directory(@Mandatory String name) {
        return directory(Path.of(name));
    }

    public @Mandatory Directory directory(@Mandatory Path path) {
        Directory directory = new Directory();
        directory.setPath(path);
        return directory;
    }

    public @Mandatory Directory directory(@Mandatory String name, Node... nodes) {
        return directory(Path.of(name), nodes);
    }

    public @Mandatory Directory directory(@Mandatory Path path, Node... nodes) {
        Directory directory = new Directory();
        directory.setPath(path);
        for (Node node : nodes) {
            if (node instanceof File file) {
                directory.getFiles().addLast(file);
            }

            if (node instanceof Directory nested) {
                directory.getDirectories().addLast(nested);
            }
        }
        statisticsCounter.count(directory, new Progress());
        return directory;
    }

    public @Mandatory FileProperties properties(long size) {
        FileProperties properties = new FileProperties();
        properties.setSize(size);
        return properties;
    }

    public @Mandatory Checksum checksum(@Mandatory String hash) {
        return checksum(Algorithm.SHA256, hash);
    }

    public @Mandatory Checksum checksum(@Mandatory Algorithm algorithm, @Mandatory String hash) {
        Checksum checksum = new Checksum();
        checksum.setAlgorithm(algorithm);
        checksum.setHash(hash);
        return checksum;
    }
}
