package cz.mg.backup.services;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.annotations.requirement.Optional;
import cz.mg.backup.entities.Directory;
import cz.mg.backup.entities.DirectoryProperties;
import cz.mg.backup.entities.File;

public @Service class StatisticsCounter {
    private static volatile @Service StatisticsCounter instance;

    public static @Service StatisticsCounter getInstance() {
        if (instance == null) {
            synchronized (Service.class) {
                if (instance == null) {
                    instance = new StatisticsCounter();
                }
            }
        }
        return instance;
    }

    private StatisticsCounter() {
    }

    /**
     * Gathers statistics for given directory and its subdirectories.
     */
    public void count(@Optional Directory directory) {
        if (directory != null) {
            initialize(directory);

            for (Directory child : directory.getDirectories()) {
                count(child);
                collect(directory, child);
            }

            for (File child : directory.getFiles()) {
                collect(directory, child);
            }
        }
    }

    private void initialize(@Mandatory Directory directory) {
        DirectoryProperties properties = directory.getProperties();
        properties.setTotalSize(0);
        properties.setTotalCount(directory.getDirectories().count() + directory.getFiles().count());
        properties.setTotalFileCount(directory.getFiles().count());
        properties.setTotalDirectoryCount(directory.getDirectories().count());
        properties.setTotalErrorCount(directory.getErrors().count());
    }

    private void collect(@Mandatory Directory parent, @Mandatory Directory child) {
        DirectoryProperties properties = parent.getProperties();
        properties.setTotalSize(properties.getTotalSize() + child.getProperties().getTotalSize());
        properties.setTotalCount(properties.getTotalCount() + child.getProperties().getTotalCount());
        properties.setTotalFileCount(properties.getTotalFileCount() + child.getProperties().getTotalFileCount());
        properties.setTotalDirectoryCount(properties.getTotalDirectoryCount() + child.getProperties().getTotalDirectoryCount());
        properties.setTotalErrorCount(properties.getTotalErrorCount() + child.getProperties().getTotalErrorCount());
    }

    public void collect(@Mandatory Directory parent, @Mandatory File child) {
        DirectoryProperties properties = parent.getProperties();
        properties.setTotalSize(properties.getTotalSize() + child.getProperties().getSize());
        properties.setTotalErrorCount(properties.getTotalErrorCount() + child.getErrors().count());
    }
}
