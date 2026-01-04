package cz.mg.backup.services;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.backup.entities.FileProperties;
import cz.mg.backup.exceptions.FileSystemException;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Date;

public @Service class FilePropertiesReader {
    private static volatile @Service FilePropertiesReader instance;

    public static @Service FilePropertiesReader getInstance() {
        if (instance == null) {
            synchronized (Service.class) {
                if (instance == null) {
                    instance = new FilePropertiesReader();
                }
            }
        }
        return instance;
    }

    private FilePropertiesReader() {
    }

    public @Mandatory FileProperties read(@Mandatory Path path) {
        try {
            BasicFileAttributes attributes = Files.readAttributes(path, BasicFileAttributes.class);
            FileProperties fileProperties = new FileProperties();
            fileProperties.setSize(attributes.size());
            fileProperties.setCreated(new Date(attributes.creationTime().toMillis()));
            fileProperties.setModified(new Date(attributes.lastModifiedTime().toMillis()));
            return fileProperties;
        } catch (Exception e) {
            throw new FileSystemException(e);
        }
    }
}
