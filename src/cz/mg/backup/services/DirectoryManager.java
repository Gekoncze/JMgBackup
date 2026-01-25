package cz.mg.backup.services;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.annotations.requirement.Optional;
import cz.mg.backup.components.Progress;
import cz.mg.backup.entities.Directory;

import java.nio.file.Path;

public @Service class DirectoryManager {
    private static volatile @Service DirectoryManager instance;

    public static @Service DirectoryManager getInstance() {
        if (instance == null) {
            synchronized (Service.class) {
                if (instance == null) {
                    instance = new DirectoryManager();
                    instance.directoryReader = DirectoryReader.getInstance();
                    instance.statisticsCounter = StatisticsCounter.getInstance();
                    instance.checksumManager = ChecksumManager.getInstance();
                    instance.directoryComparator = DirectoryComparator.getInstance();
                    instance.pathConverter = PathConverter.getInstance();
                }
            }
        }
        return instance;
    }

    private @Service DirectoryReader directoryReader;
    private @Service StatisticsCounter statisticsCounter;
    private @Service ChecksumManager checksumManager;
    private @Service DirectoryComparator directoryComparator;
    private @Service PathConverter pathConverter;

    private DirectoryManager() {
    }

    /**
     * Reloads given directory.
     * Checksums are retained where possible.
     */
    public @Mandatory Directory reload(
        @Optional Directory directory,
        @Mandatory Path path,
        @Mandatory Progress progress
    ) {
        var checksums = checksumManager.collect(directory, progress);
        directory = directoryReader.read(path, progress);
        pathConverter.computeRelativePaths(directory, progress);
        statisticsCounter.count(directory, progress);
        checksumManager.restore(directory, checksums, progress);
        return directory;
    }

    /**
     * Compares given directories recursively.
     * If one directory is null, then the other directory will have its compare errors cleared recursively.
     * Statistics are updated afterward for both directories.
     */
    public void compare(
        @Optional Directory a,
        @Optional Directory b,
        @Mandatory Progress progress
    ) {
        if (a != null && b != null) {
            directoryComparator.compare(a, b, progress);
        } else if (a != null) {
            directoryComparator.compare(a, a, progress);
        } else if (b != null) {
            directoryComparator.compare(b, b, progress);
        }

        statisticsCounter.count(a, progress);
        statisticsCounter.count(b, progress);
    }
}
