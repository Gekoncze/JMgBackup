package cz.mg.backup.services;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.annotations.requirement.Optional;
import cz.mg.backup.entities.Directory;
import cz.mg.backup.entities.File;
import cz.mg.backup.exceptions.CompareException;
import cz.mg.collections.map.Map;
import cz.mg.collections.pair.Pair;
import cz.mg.collections.pair.ReadablePair;

import java.nio.file.Path;

public @Service class DirectoryComparator {
    private static volatile @Service DirectoryComparator instance;

    public static @Service DirectoryComparator getInstance() {
        if (instance == null) {
            synchronized (Service.class) {
                if (instance == null) {
                    instance = new DirectoryComparator();
                    instance.fileComparator = FileComparator.getInstance();
                    instance.taskService = TaskService.getInstance();
                }
            }
        }
        return instance;
    }

    private @Service FileComparator fileComparator;
    private @Service TaskService taskService;

    private DirectoryComparator() {
    }

    public void compare(@Optional Directory first, @Optional Directory second) {
        if (first != null) {
            clearCompareErrors(first);
        }

        if (second != null) {
            clearCompareErrors(second);
        }

        compareRecursively(first, second);
    }

    private void compareRecursively(@Optional Directory first, @Optional Directory second) {
        compareDirectories(first, second);
        compareFiles(first, second);
    }

    private void compareDirectories(@Optional Directory first, @Optional Directory second) {
        Map<Path, Pair<Directory, Directory>> map = new Map<>();

        if (first != null) {
            for (Directory child : first.getDirectories()) {
                taskService.update();
                Pair<Directory, Directory> pair = map.getOrCreate(child.getPath().getFileName(), Pair::new);
                pair.setKey(child);
                clearCompareErrors(child);
            }
        }

        if (second != null) {
            for (Directory child : second.getDirectories()) {
                taskService.update();
                Pair<Directory, Directory> pair = map.getOrCreate(child.getPath().getFileName(), Pair::new);
                pair.setValue(child);
                clearCompareErrors(child);
            }
        }

        for (ReadablePair<Path, Pair<Directory, Directory>> entry : map) {
            taskService.update();
            Pair<Directory, Directory> pair = entry.getValue();
            if (pair.getKey() != null && pair.getValue() != null) {
                compareRecursively(pair.getKey(), pair.getValue());
            } else if (pair.getKey() != null) {
                pair.getKey().getErrors().addLast(new CompareException("Missing corresponding directory."));
                compareRecursively(pair.getKey(), pair.getValue());
            } else if (pair.getValue() != null) {
                pair.getValue().getErrors().addLast(new CompareException("Missing corresponding directory."));
                compareRecursively(pair.getKey(), pair.getValue());
            }

            updateTotalErrorCount(first, pair.getKey());
            updateTotalErrorCount(second, pair.getValue());
        }
    }

    private void compareFiles(@Optional Directory first, @Optional Directory second) {
        Map<Path, Pair<File, File>> map = new Map<>();

        if (first != null) {
            for (File child : first.getFiles()) {
                taskService.update();
                Pair<File, File> pair = map.getOrCreate(child.getPath().getFileName(), Pair::new);
                pair.setKey(child);
                clearCompareErrors(child);
            }
        }

        if (second != null) {
            for (File child : second.getFiles()) {
                taskService.update();
                Pair<File, File> pair = map.getOrCreate(child.getPath().getFileName(), Pair::new);
                pair.setValue(child);
                clearCompareErrors(child);
            }
        }

        for (ReadablePair<Path, Pair<File, File>> entry : map) {
            taskService.update();
            Pair<File, File> pair = entry.getValue();
            if (pair.getKey() != null && pair.getValue() != null) {
                fileComparator.compare(pair.getKey(), pair.getValue());
            } else if (pair.getKey() != null) {
                pair.getKey().getErrors().addLast(new CompareException("Missing corresponding file."));
            } else if (pair.getValue() != null) {
                pair.getValue().getErrors().addLast(new CompareException("Missing corresponding file."));
            }

            updateTotalErrorCount(first, pair.getKey());
            updateTotalErrorCount(second, pair.getValue());
        }
    }

    private void clearCompareErrors(@Mandatory Directory directory) {
        directory.getErrors().removeIf(e -> e instanceof CompareException);
        directory.getProperties().setTotalErrorCount(0);
    }

    private void clearCompareErrors(@Mandatory File file) {
        file.getErrors().removeIf(e -> e instanceof CompareException);
    }

    private void updateTotalErrorCount(@Optional Directory parent, @Optional File child) {
        if (parent != null && child != null) {
            parent.getProperties().setTotalErrorCount(
                parent.getProperties().getTotalErrorCount() + child.getErrors().count()
            );
        }
    }

    private void updateTotalErrorCount(@Optional Directory parent, @Optional Directory child) {
        if (parent != null && child != null) {
            parent.getProperties().setTotalErrorCount(
                parent.getProperties().getTotalErrorCount() + child.getProperties().getTotalErrorCount()
            );
        }
    }
}
