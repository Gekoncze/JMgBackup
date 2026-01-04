package cz.mg.backup.gui.services;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.backup.exceptions.FileSystemException;
import cz.mg.backup.exceptions.PlatformException;

import java.awt.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public @Service class FileManager {
    private static volatile @Service FileManager instance;

    public static @Service FileManager getInstance() {
        if (instance == null) {
            synchronized (Service.class) {
                if (instance == null) {
                    instance = new FileManager();
                }
            }
        }
        return instance;
    }

    private FileManager() {
    }

    public void open(@Mandatory Path path) {
        if (!Desktop.isDesktopSupported()) {
            throw new PlatformException("File manager is not supported on this platform.");
        }

        try {
            if (!Files.exists(path)) {
                throw new FileSystemException("Path '" + path + "' does not exist.");
            }

            if (Files.isDirectory(path)) {
                Desktop.getDesktop().open(path.toFile());
                return;
            }

            if (path.getParent() != null && Files.exists(path.getParent()) && Files.isDirectory(path.getParent())) {
                Desktop.getDesktop().open(path.getParent().toFile());
                return;
            }

            throw new FileSystemException("Could not open path '" + path + "'.");
        } catch (IOException e) {
            throw new FileSystemException("Could not open path '" + path + "'.", e);
        }
    }
}
