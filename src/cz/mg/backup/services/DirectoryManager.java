package cz.mg.backup.services;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.annotations.requirement.Optional;
import cz.mg.backup.components.Progress;
import cz.mg.backup.entities.Directory;
import cz.mg.backup.services.comparator.DirectoryComparator;

import java.nio.file.Path;

public @Service class DirectoryManager {
    private static volatile @Service DirectoryManager instance;

    public static @Service DirectoryManager getInstance() {
        if (instance == null) {
            synchronized (Service.class) {
                if (instance == null) {
                    instance = new DirectoryManager();
                    instance.directoryReader = DirectoryReader.getInstance();
                    instance.checksumManager = ChecksumManager.getInstance();
                    instance.directoryComparator = DirectoryComparator.getInstance();
                }
            }
        }
        return instance;
    }

    private @Service DirectoryReader directoryReader;
    private @Service ChecksumManager checksumManager;
    private @Service DirectoryComparator directoryComparator;

    private DirectoryManager() {
    }

    /**
     * Loads directory tree from given path.
     */
    public @Optional Directory load(@Optional Path path, @Mandatory Progress progress) {
        if (path != null) {
            return directoryReader.read(path, progress);
        } else {
            return null;
        }
    }

    /**
     * Reloads given directory tree.
     * Checksums are retained for unchanged files.
     */
    public void reload(@Mandatory Directory directory, @Mandatory Progress progress) {
        var checksums = checksumManager.collect(directory, progress);
        Directory freshDirectory = directoryReader.read(directory.getPath(), progress);
        checksumManager.restore(freshDirectory, checksums, progress);
        directory.setDirectories(freshDirectory.getDirectories());
        directory.setFiles(freshDirectory.getFiles());
        directory.setProperties(freshDirectory.getProperties());
    }

    /**
     * Compares given directory trees.
     * If one directory tree is null, then the other directory tree will have its compare errors cleared.
     */
    public void compare(@Optional Directory left, @Optional Directory right, @Mandatory Progress progress) {
        if (left != null && right != null) {
            directoryComparator.compare(left, right, progress);
        } else if (left != null) {
            directoryComparator.compare(left, left, progress);
        } else if (right != null) {
            directoryComparator.compare(right, right, progress);
        }
    }
}
