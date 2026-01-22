package cz.mg.backup.services;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.backup.components.Progress;
import cz.mg.backup.entities.Directory;

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

    /**
     * Reads directory tree from given path.
     * Files included.
     * Symbolic links skipped, except for the explicitly given path.
     * Files and directories are sorted alphabetically.
     */
    public @Mandatory Directory read(@Mandatory Path path, @Mandatory Progress progress) {
        Directory directory = new Directory();
        directory.setPath(path);
        read(directory, progress);
        return directory;
    }

    /**
     * Reads directory tree from its path.
     * Files included.
     * Symbolic links skipped, except for the explicitly given path.
     * Files and directories are sorted alphabetically.
     */
    public void read(@Mandatory Directory directory, @Mandatory Progress progress) {
        directory.getDirectories().clear();
        directory.getFiles().clear();
        directory.getErrors().clear();

        try (DirectoryStream<Path> childPaths = Files.newDirectoryStream(directory.getPath())) {
            for (Path childPath : childPaths) {
                progress.step();
                try {
                    read(directory, childPath, progress);
                } catch (Exception e) {
                    directory.getErrors().addLast(e);
                }
            }
        } catch (Exception e) {
            directory.getErrors().addLast(e);
        }

        sort.sort(directory, progress.nest("Sort directory"));
    }

    private void read(@Mandatory Directory directory, @Mandatory Path childPath, @Mandatory Progress progress) {
        if (!Files.isSymbolicLink(childPath)) {
            if (Files.isDirectory(childPath)) {
                directory.getDirectories().addLast(
                    read(childPath, progress)
                );
            } else {
                directory.getFiles().addLast(
                    fileReader.read(childPath)
                );
            }
        }
    }
}
