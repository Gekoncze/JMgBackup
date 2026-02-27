package cz.mg.backup.services;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.backup.components.Progress;
import cz.mg.backup.entities.Algorithm;
import cz.mg.backup.entities.Directory;
import cz.mg.backup.entities.File;
import cz.mg.backup.entities.Node;
import cz.mg.backup.exceptions.MissingException;
import cz.mg.collections.list.List;

import java.nio.file.Path;
import java.util.Objects;

public @Service class FileBackup {
    private static final String DESCRIPTION = "Copy files";

    private static volatile @Service FileBackup instance;

    public static @Service FileBackup getInstance() {
        if (instance == null) {
            synchronized (Service.class) {
                if (instance == null) {
                    instance = new FileBackup();
                    instance.treeIterator = TreeIterator.getInstance();
                    instance.fileManager = FileManager.getInstance();
                    instance.directoryManager = DirectoryManager.getInstance();
                }
            }
        }
        return instance;
    }

    private @Service TreeIterator treeIterator;
    private @Service FileManager fileManager;
    private @Service DirectoryManager directoryManager;

    public void copyMissingFiles(
        @Mandatory List<Node> nodes,
        @Mandatory Directory source,
        @Mandatory Directory target,
        @Mandatory Algorithm algorithm,
        @Mandatory Progress progress
    ) {
        if (Objects.equals(source.getPath(), target.getPath())) {
            throw new IllegalArgumentException("Source and target cannot be the same directory.");
        }

        treeIterator.forEachFile(
            nodes,
            file -> copyFileIfMissing(file, target, algorithm, progress),
            progress,
            DESCRIPTION
        );

        directoryManager.reload(target, progress.nest());
        progress.unnest();
    }

    private void copyFileIfMissing(
        @Mandatory File file,
        @Mandatory Directory target,
        @Mandatory Algorithm algorithm,
        @Mandatory Progress progress
    ) {
        if (isMissing(file)) {
            Path sourceFilePath = file.getPath();
            Path targetFilePath = target.getPath().resolve(file.getRelativePath());
            fileManager.copy(sourceFilePath, targetFilePath, algorithm, progress.nest());
            progress.unnest();
        }
    }

    private boolean isMissing(@Mandatory File file) {
        return file.getError() instanceof MissingException;
    }
}
