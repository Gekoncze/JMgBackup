package cz.mg.backup.services;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.backup.components.Progress;
import cz.mg.backup.entities.Directory;

import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;

public @Service class DirectoryReader {
    private static final String DESCRIPTION = "Load directory";

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
     * Reads directory tree with files from given path.
     * Symbolic links are skipped, except for given path.
     * Files and directories are sorted alphabetically.
     */
    public @Mandatory Directory read(@Mandatory Path path, @Mandatory Progress progress) {
        progress.setDescription(DESCRIPTION + " " + path);
        progress.setLimit(0L);
        progress.setValue(0L);
        return readDirectoryRecursively(path, progress);
    }

    private @Mandatory Directory readDirectoryRecursively(@Mandatory Path path, @Mandatory Progress progress) {
        Directory directory = new Directory();
        directory.setPath(path);

        try (DirectoryStream<Path> nestedPaths = Files.newDirectoryStream(directory.getPath())) {
            for (Path nestedPath : nestedPaths) {
                try {
                    readChildRecursively(directory, nestedPath, progress);
                } catch (Exception e) {
                    directory.getErrors().addLast(e);
                }
                progress.step();
            }
        } catch (Exception e) {
            directory.getErrors().addLast(e);
        }

        sort.sort(directory, progress.nest());
        progress.unnest();

        return directory;
    }

    private void readChildRecursively(@Mandatory Directory directory, @Mandatory Path nestedPath, @Mandatory Progress progress) {
        if (!Files.isSymbolicLink(nestedPath)) {
            if (Files.isDirectory(nestedPath)) {
                directory.getDirectories().addLast(
                    readDirectoryRecursively(nestedPath, progress)
                );
            } else {
                directory.getFiles().addLast(
                    fileReader.read(nestedPath)
                );
            }
        }
    }
}
