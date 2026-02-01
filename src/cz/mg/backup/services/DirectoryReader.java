package cz.mg.backup.services;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.backup.components.Progress;
import cz.mg.backup.entities.Directory;
import cz.mg.backup.entities.File;

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
                    instance.propertiesCollector = DirectoryPropertiesCollector.getInstance();
                }
            }
        }
        return instance;
    }

    private @Service FileReader fileReader;
    private @Service DirectorySort sort;
    private @Service DirectoryPropertiesCollector propertiesCollector;

    private DirectoryReader() {
    }

    /**
     * Reads directory tree with files from given path.
     * Symbolic links are skipped, except for given path.
     * Directory properties are collected.
     * Files and directories are sorted alphabetically.
     */
    public @Mandatory Directory read(@Mandatory Path path, @Mandatory Progress progress) {
        progress.setDescription(DESCRIPTION + " " + path);
        progress.setLimit(0L);
        progress.setValue(0L);
        return readDirectoryRecursively(
            path,
            path.getFileName() != null ? path.getFileName() : Path.of(""),
            progress
        );
    }

    private @Mandatory Directory readDirectoryRecursively(
        @Mandatory Path path,
        @Mandatory Path relativePath,
        @Mandatory Progress progress
    ) {
        Directory directory = new Directory();
        directory.setPath(path);
        directory.setRelativePath(relativePath);

        try (DirectoryStream<Path> nestedPaths = Files.newDirectoryStream(directory.getPath())) {
            for (Path nestedPath : nestedPaths) {
                progress.setDescription(DESCRIPTION + " " + nestedPath.toString());
                try {
                    readChildRecursively(directory, nestedPath, progress);
                } catch (Exception e) {
                    directory.setError(e);
                }
                progress.step();
            }
        } catch (Exception e) {
            directory.setError(e);
        }

        propertiesCollector.collect(directory);
        sort.sort(directory, progress.nest());
        progress.unnest();

        return directory;
    }

    private void readChildRecursively(
        @Mandatory Directory directory,
        @Mandatory Path childPath,
        @Mandatory Progress progress
    ) {
        if (!Files.isSymbolicLink(childPath)) {
            Path relativeChildPath = directory.getRelativePath().resolve(childPath.getFileName());
            if (Files.isDirectory(childPath)) {
                Directory child = readDirectoryRecursively(childPath, relativeChildPath, progress);
                directory.getDirectories().addLast(child);
            } else {
                File child = fileReader.read(childPath);
                child.setRelativePath(relativeChildPath);
                directory.getFiles().addLast(child);
            }
        }
    }
}
