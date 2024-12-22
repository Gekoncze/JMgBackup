package cz.mg.backup.services;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.annotations.requirement.Optional;
import cz.mg.backup.components.Progress;
import cz.mg.backup.entities.Directory;
import cz.mg.backup.entities.File;
import cz.mg.backup.entities.Node;

import java.util.function.Consumer;

public @Service class DirectoryService {
    private static volatile @Service DirectoryService instance;

    public static @Service DirectoryService getInstance() {
        if (instance == null) {
            synchronized (Service.class) {
                if (instance == null) {
                    instance = new DirectoryService();
                }
            }
        }
        return instance;
    }

    private DirectoryService() {
    }

    public void forEachFile(
        @Optional Directory directory,
        @Mandatory Consumer<File> consumer,
        @Mandatory Progress progress
    ) {
        forEachNode(directory, node -> {
            if (node instanceof File wanted) {
                consumer.accept(wanted);
            }
        }, progress);
    }

    public void forEachDirectory(
        @Optional Directory directory,
        @Mandatory Consumer<Directory> consumer,
        @Mandatory Progress progress
    ) {
        forEachNode(directory, node -> {
            if (node instanceof Directory wanted) {
                consumer.accept(wanted);
            }
        }, progress);
    }

    public void forEachNode(
        @Optional Directory directory,
        @Mandatory Consumer<Node> consumer,
        @Mandatory Progress progress
    ) {
        if (directory != null) {
            consumer.accept(directory);
            progress.step();

            for (File file : directory.getFiles()) {
                consumer.accept(file);
                progress.step();
            }

            for (Directory subdirectory : directory.getDirectories()) {
                forEachNode(subdirectory, consumer, progress);
            }
        }
    }
}
