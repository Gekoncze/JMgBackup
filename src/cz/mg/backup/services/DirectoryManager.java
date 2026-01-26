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
     * Loads directory from given path.
     */
    public @Optional Directory load(@Optional Path path, @Mandatory Progress progress) {
        if (path != null) {
            Directory directory = directoryReader.read(path, progress);
            pathConverter.computeRelativePaths(directory, progress);
            statisticsCounter.count(directory, progress);
            return directory;
        } else {
            return null;
        }
    }

    /**
     * Reloads given directory.
     * Checksums are retained for unchanged files.
     */
    public void reload(@Mandatory Directory directory, @Mandatory Progress progress) {
        var checksums = checksumManager.collect(directory, progress);
        Directory freshDirectory = directoryReader.read(directory.getPath(), progress);
        pathConverter.computeRelativePaths(freshDirectory, progress);
        statisticsCounter.count(freshDirectory, progress);
        checksumManager.restore(freshDirectory, checksums, progress);
        directory.setDirectories(freshDirectory.getDirectories());
        directory.setFiles(freshDirectory.getFiles());
        directory.setProperties(freshDirectory.getProperties());
    }

    /**
     * Compares given directories recursively.
     * If one directory is null, then the other directory will have its compare errors cleared recursively.
     * Statistics are updated afterward for both directories.
     */
    public void compare(@Optional Directory left, @Optional Directory right, @Mandatory Progress progress) {
        if (left != null && right != null) {
            directoryComparator.compare(left, right, progress);
        } else if (left != null) {
            directoryComparator.compare(left, left, progress);
        } else if (right != null) {
            directoryComparator.compare(right, right, progress);
        }

        statisticsCounter.count(left, progress);
        statisticsCounter.count(right, progress);
    }
}
