package cz.mg.backup.services;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.annotations.requirement.Optional;
import cz.mg.backup.components.Progress;
import cz.mg.backup.entities.Directory;
import cz.mg.backup.entities.File;
import cz.mg.backup.exceptions.CompareException;
import cz.mg.backup.exceptions.MismatchException;
import cz.mg.backup.exceptions.MissingException;
import cz.mg.collections.map.Map;
import cz.mg.collections.pair.Pair;
import cz.mg.collections.pair.ReadablePair;

import java.nio.file.Path;
import java.util.Objects;

public @Service class DirectoryComparator {
    private static final String DESCRIPTION = "Compare";

    private static volatile @Service DirectoryComparator instance;

    public static @Service DirectoryComparator getInstance() {
        if (instance == null) {
            synchronized (Service.class) {
                if (instance == null) {
                    instance = new DirectoryComparator();
                    instance.fileComparator = FileComparator.getInstance();
                }
            }
        }
        return instance;
    }

    private @Service FileComparator fileComparator;

    private DirectoryComparator() {
    }

    /**
     * Compares given directories and stores compare exceptions in respective files and directories.
     */
    public void compare(
        @Optional Directory first,
        @Optional Directory second,
        @Mandatory Progress progress
    ) {
        progress.setDescription(DESCRIPTION);
        progress.setLimit(estimate(first, second));
        comparePairedDirectories(first, second, progress);
    }

    private void compareDirectories(
        @Optional Directory first,
        @Optional Directory second,
        @Mandatory Progress progress
    ) {
        Map<Path, Pair<Directory, Directory>> map = new Map<>();

        // collect first directories to pairs
        if (first != null) {
            for (Directory child : first.getDirectories()) {
                Pair<Directory, Directory> pair = map.getOrCreate(child.getPath().getFileName(), Pair::new);
                pair.setKey(child);
                progress.step();
            }
        }

        // collect second directories to pairs
        if (second != null) {
            for (Directory child : second.getDirectories()) {
                Pair<Directory, Directory> pair = map.getOrCreate(child.getPath().getFileName(), Pair::new);
                pair.setValue(child);
                progress.step();
            }
        }

        // compare paired directories
        for (ReadablePair<Path, Pair<Directory, Directory>> entry : map) {
            Pair<Directory, Directory> pair = entry.getValue();
            comparePairedDirectories(pair.getKey(), pair.getValue(), progress);
        }
    }

    private void comparePairedDirectories(
        @Optional Directory first,
        @Optional Directory second,
        @Mandatory Progress progress
    ) {
        clearCompareErrors(first);
        clearCompareErrors(second);

        if (first != null && second == null) {
            first.getErrors().addLast(new MissingException("Missing corresponding directory."));
            first.getProperties().setTotalErrorCount(first.getProperties().getTotalErrorCount() + 1);
            progress.step();
        }

        if (first == null && second != null) {
            second.getErrors().addLast(new MissingException("Missing corresponding directory."));
            second.getProperties().setTotalErrorCount(second.getProperties().getTotalErrorCount() + 1);
            progress.step();
        }

        if (first != null && second != null) {
            if (!Objects.equals(first.getPath().getFileName(), second.getPath().getFileName())) {
                first.getErrors().addLast(new MismatchException("Directory name differs."));
                first.getProperties().setTotalErrorCount(first.getProperties().getTotalErrorCount() + 1);
                second.getErrors().addLast(new MismatchException("Directory name differs."));
                second.getProperties().setTotalErrorCount(second.getProperties().getTotalErrorCount() + 1);
            }
            progress.step(2);
        }

        compareDirectories(first, second, progress);
        compareFiles(first, second, progress);
    }

    private void compareFiles(
        @Optional Directory first,
        @Optional Directory second,
        @Mandatory Progress progress
    ) {
        Map<Path, Pair<File, File>> map = new Map<>();

        // collect first files to pairs
        if (first != null) {
            for (File child : first.getFiles()) {
                Pair<File, File> pair = map.getOrCreate(child.getPath().getFileName(), Pair::new);
                pair.setKey(child);
                progress.step();
            }
        }

        // collect second files to pairs
        if (second != null) {
            for (File child : second.getFiles()) {
                Pair<File, File> pair = map.getOrCreate(child.getPath().getFileName(), Pair::new);
                pair.setValue(child);
                progress.step();
            }
        }

        // compare paired files
        for (ReadablePair<Path, Pair<File, File>> entry : map) {
            Pair<File, File> pair = entry.getValue();
            comparePairedFiles(pair.getKey(), pair.getValue(), progress);
        }
    }

    private void comparePairedFiles(@Optional File first, @Optional File second, @Mandatory Progress progress) {
        clearCompareErrors(first);
        clearCompareErrors(second);

        if (first != null && second != null) {
            fileComparator.compare(first, second);
            progress.step(2);
        } else if (first != null) {
            first.getErrors().addLast(new MissingException("Missing corresponding file."));
            progress.step();
        } else if (second != null) {
            second.getErrors().addLast(new MissingException("Missing corresponding file."));
            progress.step();
        }
    }

    private void clearCompareErrors(@Optional Directory directory) {
        if (directory != null) {
            directory.getErrors().removeIf(e -> e instanceof CompareException);
        }
    }

    private void clearCompareErrors(@Optional File file) {
        if (file != null) {
            file.getErrors().removeIf(e -> e instanceof CompareException);
        }
    }

    private long estimate(@Optional Directory first, @Optional Directory second) {
        long firstTotal = first != null ? first.getProperties().getTotalCount() : 0L;
        long secondTotal = second != null ? second.getProperties().getTotalCount() : 0L;
        return 2L * (firstTotal + secondTotal) + 2L;
    }
}
