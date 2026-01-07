package cz.mg.backup.services;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.requirement.Mandatory;

import java.nio.file.Path;
import java.util.Iterator;
import java.util.Objects;

public @Service class PathConverter {
    private static volatile @Service PathConverter instance;

    public static @Service PathConverter getInstance() {
        if (instance == null) {
            synchronized (Service.class) {
                if (instance == null) {
                    instance = new PathConverter();
                }
            }
        }
        return instance;
    }

    private PathConverter() {
    }

    public @Mandatory Path sourcePathToTargetPath(
        @Mandatory Path filePath,
        @Mandatory Path sourceDirectoryPath,
        @Mandatory Path targetDirectoryPath
    ) {
        if (filePath.getNameCount() == 0 || filePath.toString().isEmpty()) {
            throw new IllegalArgumentException("File path cannot be empty.");
        }

        Iterator<Path> filePathIterator = filePath.iterator();
        Iterator<Path> sourcePathIterator = sourceDirectoryPath.iterator();
        if (!sourceDirectoryPath.toString().isEmpty()) {
            while (filePathIterator.hasNext() && sourcePathIterator.hasNext()) {
                Path filePathElement = filePathIterator.next();
                Path sourcePathElement = sourcePathIterator.next();
                if (!filePathIterator.hasNext() || !Objects.equals(filePathElement, sourcePathElement)) {
                    throw new IllegalArgumentException(
                        "File '" + filePath + "' is not in directory '" + sourceDirectoryPath + "'."
                    );
                }
            }
        }
        Path targetFilePath = targetDirectoryPath;
        while (filePathIterator.hasNext()) {
            targetFilePath = targetFilePath.resolve(filePathIterator.next());
        }
        return targetFilePath;
    }
}
