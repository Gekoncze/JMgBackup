package cz.mg.backup.services;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.backup.entities.Directory;
import cz.mg.backup.entities.DirectoryProperties;
import cz.mg.backup.entities.File;

public @Service class DirectoryPropertiesCollector {
    private static volatile @Service DirectoryPropertiesCollector instance;

    public static @Service DirectoryPropertiesCollector getInstance() {
        if (instance == null) {
            synchronized (Service.class) {
                if (instance == null) {
                    instance = new DirectoryPropertiesCollector();
                }
            }
        }
        return instance;
    }

    private DirectoryPropertiesCollector() {
    }

    /**
     * Collect properties for given directory using values from its sub-directories and files.
     */
    public void collect(@Mandatory Directory directory) {
        initialize(directory);

        for (Directory child : directory.getDirectories()) {
            collectChild(directory, child);
        }

        for (File child : directory.getFiles()) {
            collectChild(directory, child);
        }
    }

    private void initialize(@Mandatory Directory directory) {
        DirectoryProperties properties = directory.getProperties();
        properties.setTotalSize(0);
        properties.setTotalCount(directory.getDirectories().count() + directory.getFiles().count());
        properties.setTotalFileCount(directory.getFiles().count());
        properties.setTotalDirectoryCount(directory.getDirectories().count());
    }

    private void collectChild(@Mandatory Directory directory, @Mandatory Directory child) {
        DirectoryProperties properties = directory.getProperties();
        properties.setTotalSize(properties.getTotalSize() + child.getProperties().getTotalSize());
        properties.setTotalCount(properties.getTotalCount() + child.getProperties().getTotalCount());
        properties.setTotalFileCount(properties.getTotalFileCount() + child.getProperties().getTotalFileCount());
        properties.setTotalDirectoryCount(properties.getTotalDirectoryCount() + child.getProperties().getTotalDirectoryCount());
    }

    public void collectChild(@Mandatory Directory directory, @Mandatory File child) {
        DirectoryProperties properties = directory.getProperties();
        properties.setTotalSize(properties.getTotalSize() + child.getProperties().getSize());
    }
}
