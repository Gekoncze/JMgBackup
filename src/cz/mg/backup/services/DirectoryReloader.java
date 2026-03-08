package cz.mg.backup.services;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.annotations.requirement.Optional;
import cz.mg.backup.components.Progress;
import cz.mg.backup.entities.Directory;

public @Service class DirectoryReloader {
    private static volatile @Service DirectoryReloader instance;

    public static @Service DirectoryReloader getInstance() {
        if (instance == null) {
            synchronized (Service.class) {
                if (instance == null) {
                    instance = new DirectoryReloader();
                    instance.directoryReader = DirectoryReader.getInstance();
                    instance.checksumManager = ChecksumManager.getInstance();
                }
            }
        }
        return instance;
    }

    private @Service DirectoryReader directoryReader;
    private @Service ChecksumManager checksumManager;

    private DirectoryReloader() {
    }

    /**
     * Reloads given directory tree.
     * Checksums are retained for unchanged files.
     */
    public void reload(@Optional Directory directory, @Mandatory Progress progress) {
        if (directory != null) {
            var checksums = checksumManager.collect(directory, progress);
            Directory freshDirectory = directoryReader.read(directory.getPath(), progress);
            checksumManager.restore(freshDirectory, checksums, progress);
            directory.setDirectories(freshDirectory.getDirectories());
            directory.setFiles(freshDirectory.getFiles());
            directory.setProperties(freshDirectory.getProperties());
        }
    }
}
