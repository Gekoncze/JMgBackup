package cz.mg.backup.services;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.backup.entities.Directory;
import cz.mg.backup.entities.File;
import cz.mg.backup.exceptions.CompareException;
import cz.mg.collections.components.Capacity;
import cz.mg.collections.map.Map;
import cz.mg.collections.pair.Pair;
import cz.mg.collections.pair.ReadablePair;

import java.nio.file.Path;

public @Service class DirectoryCompareService {
    private static volatile @Service DirectoryCompareService instance;

    public static @Service DirectoryCompareService getInstance() {
        if (instance == null) {
            synchronized (Service.class) {
                if (instance == null) {
                    instance = new DirectoryCompareService();
                    instance.fileCompareService = FileCompareService.getInstance();
                }
            }
        }
        return instance;
    }

    private @Service FileCompareService fileCompareService;

    private DirectoryCompareService() {
    }

    public void compare(@Mandatory Directory first, @Mandatory Directory second) {
        first.getErrors().removeIf(e -> e instanceof CompareException);
        second.getErrors().removeIf(e -> e instanceof CompareException);
        compareDirectories(first, second);
        compareFiles(first, second);
        propagateErrors(first);
        propagateErrors(second);
    }

    private void compareDirectories(@Mandatory Directory first, @Mandatory Directory second) {
        Map<Path, Pair<Directory, Directory>> map = new Map<>(new Capacity(100));

        for (Directory child : first.getDirectories()) {
            Pair<Directory, Directory> pair = map.getOrCreate(child.getPath().getFileName(), Pair::new);
            pair.setKey(child);
        }

        for (Directory child : second.getDirectories()) {
            Pair<Directory, Directory> pair = map.getOrCreate(child.getPath().getFileName(), Pair::new);
            pair.setValue(child);
        }

        for (ReadablePair<Path, Pair<Directory, Directory>> entry : map) {
            Pair<Directory, Directory> pair = entry.getValue();
            if (pair.getKey() != null && pair.getValue() != null) {
                compare(pair.getKey(), pair.getValue());
            } else if (pair.getKey() != null) {
                pair.getKey().getErrors().addLast(new CompareException("Missing corresponding directory."));
            } else if (pair.getValue() != null) {
                pair.getValue().getErrors().addLast(new CompareException("Missing corresponding directory."));
            }
        }
    }

    private void compareFiles(@Mandatory Directory first, @Mandatory Directory second) {
        Map<Path, Pair<File, File>> map = new Map<>(new Capacity(100));

        for (File child : first.getFiles()) {
            Pair<File, File> pair = map.getOrCreate(child.getPath().getFileName(), Pair::new);
            pair.setKey(child);
        }

        for (File child : second.getFiles()) {
            Pair<File, File> pair = map.getOrCreate(child.getPath().getFileName(), Pair::new);
            pair.setValue(child);
        }

        for (ReadablePair<Path, Pair<File, File>> entry : map) {
            Pair<File, File> pair = entry.getValue();
            if (pair.getKey() != null && pair.getValue() != null) {
                fileCompareService.compare(pair.getKey(), pair.getValue());
            } else if (pair.getKey() != null) {
                pair.getKey().getErrors().addLast(new CompareException("Missing corresponding file."));
            } else if (pair.getValue() != null) {
                pair.getValue().getErrors().addLast(new CompareException("Missing corresponding file."));
            }
        }
    }

    private void propagateErrors(@Mandatory Directory directory) {
        Exception error = null;

        for (Directory child : directory.getDirectories()) {
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
            directory.getErrors().addLast(
                new CompareException("Child node has an error.", error)
            );
        }
    }
}
