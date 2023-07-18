package cz.mg.backup.services;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.requirement.Mandatory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public @Service class FileSizeReader {
    private static volatile @Service FileSizeReader instance;

    public static @Service FileSizeReader getInstance() {
        if (instance == null) {
            synchronized (Service.class) {
                if (instance == null) {
                    instance = new FileSizeReader();
                }
            }
        }
        return instance;
    }

    private FileSizeReader() {
    }

    public long read(@Mandatory Path path) {
        try {
            return Files.size(path);
        } catch (IOException e) {
            throw new RuntimeException("Could not read file size.", e);
        }
    }
}
