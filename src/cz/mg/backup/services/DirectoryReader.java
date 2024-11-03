package cz.mg.backup.services;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.backup.components.Progress;
import cz.mg.backup.entities.Directory;
import cz.mg.backup.entities.DirectoryProperties;
import cz.mg.backup.entities.File;
import cz.mg.backup.entities.Settings;

import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;

public @Service class DirectoryReader {
    private static volatile @Service DirectoryReader instance;

    public static @Service DirectoryReader getInstance() {
        if (instance == null) {
            synchronized (Service.class) {
                if (instance == null) {
                    instance = new DirectoryReader();
                    instance.fileReader = FileReader.getInstance();
                    instance.sort = DirectorySort.getInstance();
                }
            }
        }
        return instance;
    }

    private @Service FileReader fileReader;
    private @Service DirectorySort sort;

    private DirectoryReader() {
    }

    public @Mandatory Directory read(
        @Mandatory Path path,
        @Mandatory Settings settings,
        @Mandatory Progress progress
    ) {
        Directory directory = new Directory();
        directory.setProperties(new DirectoryProperties());
        directory.setPath(path);

        try (DirectoryStream<Path> childPaths = Files.newDirectoryStream(path)) {
            for (Path childPath : childPaths) {
                progress.step();
                try {
                    read(directory, childPath, settings, progress);
                } catch (Exception e) {
                    directory.getErrors().addLast(e);
                }
            }
        } catch (Exception e) {
            directory.getErrors().addLast(e);
        }

        sort.sort(directory, progress.nest("Sort directory"));

        return directory;
    }

    private void read(
        @Mandatory Directory directory,
        @Mandatory Path childPath,
        @Mandatory Settings settings,
        @Mandatory Progress progress
    ) {
        if (!Files.isSymbolicLink(childPath)) {
            if (Files.isDirectory(childPath)) {
                Directory childDirectory = read(childPath, settings, progress);
                directory.getDirectories().addLast(childDirectory);
                directory.getProperties().add(childDirectory.getProperties());
            } else {
                File childFile = fileReader.read(childPath);
                directory.getFiles().addLast(childFile);
                directory.getProperties().add(childFile.getProperties());
            }
        }
    }
}
