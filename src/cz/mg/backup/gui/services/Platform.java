package cz.mg.backup.gui.services;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.backup.exceptions.FileSystemException;
import cz.mg.backup.exceptions.PlatformException;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;

public @Service class Platform {
    private static volatile @Service Platform instance;

    public static @Service Platform getInstance() {
        if (instance == null) {
            synchronized (Service.class) {
                if (instance == null) {
                    instance = new Platform();
                }
            }
        }
        return instance;
    }

    private Platform() {
    }

    public void openFileManager(@Mandatory Path path) {
        validate();

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

    public void openBrowser(@Mandatory String url) {
        validate();

        try {
            Desktop.getDesktop().browse(new URI(url));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void validate() {
        if (!Desktop.isDesktopSupported()) {
            throw new PlatformException("Desktop operations are not supported on your system.");
        }
    }
}
