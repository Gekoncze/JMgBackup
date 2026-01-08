package cz.mg.backup.services;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.backup.components.Progress;
import cz.mg.backup.entities.Directory;

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
                    instance.directoryService = DirectoryService.getInstance();
                }
            }
        }
        return instance;
    }

    private @Service DirectoryService directoryService;

    private PathConverter() {
    }

    public void computeRelativePaths(@Mandatory Directory directory, @Mandatory Progress progress) {
        directoryService.forEachNode(directory, node -> {
            node.setRelativePath(toRelativePath(node.getPath(), directory.getPath()));
        }, progress);
    }

    public @Mandatory Path toRelativePath(@Mandatory Path nodePath, @Mandatory Path directoryPath) {
        if (Objects.equals(nodePath, directoryPath)) {
            return Path.of("");
        }

        if (nodePath.getNameCount() == 0 || nodePath.toString().isEmpty()) {
            throw new IllegalArgumentException("Path cannot be empty.");
        }

        Iterator<Path> filePathIterator = nodePath.iterator();
        Iterator<Path> directoryPathIterator = directoryPath.iterator();
        if (!directoryPath.toString().isEmpty()) {
            while (filePathIterator.hasNext() && directoryPathIterator.hasNext()) {
                Path filePathElement = filePathIterator.next();
                Path directoryPathElement = directoryPathIterator.next();
                if (!filePathIterator.hasNext() || !Objects.equals(filePathElement, directoryPathElement)) {
                    throw new IllegalArgumentException(
                        "Path '" + nodePath + "' is not in directory '" + directoryPath + "'."
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
