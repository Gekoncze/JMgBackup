package cz.mg.backup.services;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.annotations.requirement.Optional;
import cz.mg.backup.entities.Directory;
import cz.mg.backup.entities.File;
import cz.mg.backup.entities.Node;
import cz.mg.backup.exceptions.CompareException;
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
                    instance.taskService = TaskService.getInstance();
                }
            }
        }
        return instance;
    }

    private @Service FileCompareService fileCompareService;
    private @Service TaskService taskService;

    private DirectoryCompareService() {
    }

    public void compare(@Optional Directory first, @Optional Directory second) {
        if (first != null && second != null) {
            compareExisting(first, second);
        } else if (first != null) {
            clearCompareErrors(first);
        } else if (second != null) {
            clearCompareErrors(second);
        }
    }

    private void compareExisting(@Mandatory Directory first, @Mandatory Directory second) {
        clearSingleCompareErrors(first);
        clearSingleCompareErrors(second);
        compareDirectories(first, second);
        compareFiles(first, second);
    }

    private void compareDirectories(@Mandatory Directory first, @Mandatory Directory second) {
        Map<Path, Pair<Directory, Directory>> map = new Map<>();

        for (Directory child : first.getDirectories()) {
            taskService.update();
            Pair<Directory, Directory> pair = map.getOrCreate(child.getPath().getFileName(), Pair::new);
            pair.setKey(child);
        }

        for (Directory child : second.getDirectories()) {
            taskService.update();
            Pair<Directory, Directory> pair = map.getOrCreate(child.getPath().getFileName(), Pair::new);
            pair.setValue(child);
        }

        for (ReadablePair<Path, Pair<Directory, Directory>> entry : map) {
            taskService.update();
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
        Map<Path, Pair<File, File>> map = new Map<>();

        for (File child : first.getFiles()) {
            taskService.update();
            Pair<File, File> pair = map.getOrCreate(child.getPath().getFileName(), Pair::new);
            pair.setKey(child);
        }

        for (File child : second.getFiles()) {
            taskService.update();
            Pair<File, File> pair = map.getOrCreate(child.getPath().getFileName(), Pair::new);
            pair.setValue(child);
        }

        for (ReadablePair<Path, Pair<File, File>> entry : map) {
            taskService.update();
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

    private void clearCompareErrors(@Mandatory Directory directory) {
        clearSingleCompareErrors(directory);

        for (Directory child : directory.getDirectories()) {
            taskService.update();
            clearCompareErrors(child);
        }

        for (File child : directory.getFiles()) {
            taskService.update();
            clearSingleCompareErrors(child);
        }
    }

    private void clearSingleCompareErrors(@Mandatory Node node) {
        node.getErrors().removeIf(e -> e instanceof CompareException);
    }
}
