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
import cz.mg.backup.services.DirectoryPropertiesCollector;
import cz.mg.backup.services.TreeIterator;

import java.nio.file.Path;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

public @Service class TestFactory {
    private static volatile @Service TestFactory instance;

    public static @Service TestFactory getInstance() {
        if (instance == null) {
            synchronized (Service.class) {
                if (instance == null) {
                    instance = new TestFactory();
                    instance.directoryPropertiesCollector = DirectoryPropertiesCollector.getInstance();
                    instance.treeIterator = TreeIterator.getInstance();
                }
            }
        }
        return instance;
    }

    private @Service DirectoryPropertiesCollector directoryPropertiesCollector;
    private @Service TreeIterator treeIterator;

    private TestFactory() {
    }

    public @Mandatory File file(@Mandatory String name) {
        Path path = Path.of(name);
        File file = new File();
        file.setPath(path);
        file.setRelativePath(path);
        return file;
    }

    public @Mandatory File file(@Mandatory String name, @Optional Exception error) {
        File file = file(name);
        file.setError(error);
        return file;
    }

    public @Mandatory File file(
        @Mandatory String name,
        @Optional FileProperties properties,
        @Optional Checksum checksum
    ) {
        File file = file(name);
        file.setProperties(properties != null ? properties : new FileProperties());
        file.setChecksum(checksum);
        return file;
    }

    public @Mandatory Directory directory(@Mandatory String name, Node... nodes) {
        Path path = Path.of(name);
        Directory directory = new Directory();
        directory.setPath(path);
        directory.setRelativePath(path);
        for (Node node : nodes) {
            if (node instanceof File file) {
                directory.getFiles().addLast(file);
            } else if (node instanceof Directory nested) {
                directory.getDirectories().addLast(nested);
            } else {
                throw new IllegalArgumentException("Unexpected node of type " + node.getClass().getSimpleName() + ".");
            }
            treeIterator.forEachNode(node, n -> resolvePaths(directory, n), new Progress(), "test");
        }
        directoryPropertiesCollector.collect(directory);
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

    private void resolvePaths(@Mandatory Directory directory, @Mandatory Node child) {
        child.setPath(Path.of(
            directory.getPath().getFileName().toString(),
            child.getPath().toString()
        ));

        child.setRelativePath(Path.of(
            directory.getPath().getFileName().toString(),
            child.getRelativePath().toString()
        ));
    }

    public @Mandatory Instant date(int year, int month, int day) {
        return date(year, month, day, 0, 0);
    }

    public @Mandatory Instant date(int year, int month, int day, int hour, int minute) {
        return LocalDateTime.of(year, month, day, hour, minute)
            .atZone(ZoneId.systemDefault())
            .toInstant();
    }
}
