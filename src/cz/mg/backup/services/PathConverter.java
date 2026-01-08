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

    public @Mandatory Path toRelativePath(@Mandatory Path filePath, @Mandatory Path directoryPath) {
        if (filePath.getNameCount() == 0 || filePath.toString().isEmpty()) {
            throw new IllegalArgumentException("File path cannot be empty.");
        }

        Iterator<Path> filePathIterator = filePath.iterator();
        Iterator<Path> directoryPathIterator = directoryPath.iterator();
        if (!directoryPath.toString().isEmpty()) {
            while (filePathIterator.hasNext() && directoryPathIterator.hasNext()) {
                Path filePathElement = filePathIterator.next();
                Path directoryPathElement = directoryPathIterator.next();
                if (!filePathIterator.hasNext() || !Objects.equals(filePathElement, directoryPathElement)) {
                    throw new IllegalArgumentException(
                        "File '" + filePath + "' is not in directory '" + directoryPath + "'."
                    );
                }
            }
        }
        Path relativePath = Path.of(filePathIterator.next().toString());
        while (filePathIterator.hasNext()) {
            relativePath = relativePath.resolve(filePathIterator.next());
        }
        return relativePath;
    }
}
