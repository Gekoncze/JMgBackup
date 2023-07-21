package cz.mg.backup.services;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.backup.entities.Directory;
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

    public @Mandatory Directory read(@Mandatory Path path, @Mandatory Settings settings) {
        Directory directory = new Directory();
        directory.setPath(path);
        try (DirectoryStream<Path> childPaths = Files.newDirectoryStream(path)) {
            for (Path childPath : childPaths) {
                try {
                    read(directory, childPath, settings);
                } catch (Exception e) {
                    directory.getErrors().addLast(e);
                }
            }
        } catch (Exception e) {
            directory.getErrors().addLast(e);
        }
        sort.sort(directory);
        return directory;
    }

    private void read(@Mandatory Directory directory, @Mandatory Path childPath, @Mandatory Settings settings) {
        if (!Files.isSymbolicLink(childPath)) {
            if (Files.isDirectory(childPath)) {
                directory.getDirectories().addLast(read(childPath, settings));
            } else {
                directory.getFiles().addLast(fileReader.read(childPath, settings));
            }
        }
    }
}
