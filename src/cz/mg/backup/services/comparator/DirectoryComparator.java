package cz.mg.backup.services.comparator;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.annotations.requirement.Optional;
import cz.mg.backup.components.Progress;
import cz.mg.backup.entities.Directory;
import cz.mg.backup.entities.File;
import cz.mg.backup.exceptions.MismatchException;
import cz.mg.backup.exceptions.MissingException;
import cz.mg.backup.exceptions.NestedException;
import cz.mg.collections.map.Map;
import cz.mg.collections.pair.Pair;
import cz.mg.collections.pair.ReadablePair;

import java.nio.file.Path;
import java.util.Objects;

public @Service class DirectoryComparator extends NodeComparator {
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
        progress.setValue(0L);
        comparePairedDirectories(first, second, progress);
    }

    private void compareDirectories(
        @Optional Directory first,
        @Optional Directory second,
        @Mandatory Progress progress
    ) {
        Map<Path, Pair<Directory, Directory>> map = new Map<>();

        // collect first directory children to pair
        if (first != null) {
            for (Directory child : first.getDirectories()) {
                Pair<Directory, Directory> pair = map.getOrCreate(child.getPath().getFileName(), Pair::new);
                pair.setKey(child);
                progress.step();
            }
        }

        // collect second directory children to pair
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
        clearCompareError(first);
        clearCompareError(second);

        if (first != null && second == null) {
            setCompareError(first, new MissingException("Missing corresponding directory."));
            progress.step();
        }

        if (first == null && second != null) {
            setCompareError(second, new MissingException("Missing corresponding directory."));
            progress.step();
        }

        if (first != null && second != null) {
            if (!Objects.equals(first.getPath().getFileName(), second.getPath().getFileName())) {
                setCompareError(first, new MismatchException("Directory name differs."));
                setCompareError(second, new MismatchException("Directory name differs."));
            }
            progress.step(2);
        }

        compareDirectories(first, second, progress);
        compareFiles(first, second, progress);
        identifyNestedError(first);
        identifyNestedError(second);
    }

    private void identifyNestedError(@Optional Directory directory) {
        if (directory != null && directory.getError() == null && hasNestedError(directory)) {
            directory.setError(new NestedException("Nested file or directory has an error."));
        }
    }

    private boolean hasNestedError(@Mandatory Directory directory) {
        for (Directory child : directory.getDirectories()) {
            if (child.getError() != null) {
                return true;
            }
        }
        for (File child : directory.getFiles()) {
            if (child.getError() != null) {
                return true;
            }
        }
        return false;
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
        clearCompareError(first);
        clearCompareError(second);

        if (first != null && second != null) {
            fileComparator.compare(first, second);
            progress.step(2);
        } else if (first != null) {
            setCompareError(first, new MissingException("Missing corresponding file."));
            progress.step();
        } else if (second != null) {
            setCompareError(second, new MissingException("Missing corresponding file."));
            progress.step();
        }
    }

    private long estimate(@Optional Directory first, @Optional Directory second) {
        long firstTotal = first != null ? first.getProperties().getTotalCount() : 0L;
        long secondTotal = second != null ? second.getProperties().getTotalCount() : 0L;
        return 2L * (firstTotal + secondTotal) + 2L;
    }
}
