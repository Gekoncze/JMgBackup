package cz.mg.backup.services;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.requirement.Optional;
import cz.mg.backup.exceptions.FileSystemException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public @Service class DirectoryWriter {
    private static volatile @Service DirectoryWriter instance;

    public static @Service DirectoryWriter getInstance() {
        if (instance == null) {
            synchronized (Service.class) {
                if (instance == null) {
                    instance = new DirectoryWriter();
                }
            }
        }
        return instance;
    }

    private DirectoryWriter() {
    }

    public void createDirectories(@Optional Path path) {
        try {
            if (path != null) {
                Files.createDirectories(path);
            }
        } catch (IOException e) {
            throw new FileSystemException("Could not create directory '" + path + "'.");
        }
    }
}
