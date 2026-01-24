package cz.mg.backup.services;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.annotations.requirement.Optional;
import cz.mg.backup.components.Progress;
import cz.mg.backup.entities.Directory;
import cz.mg.backup.entities.File;
import cz.mg.backup.entities.Node;
import cz.mg.collections.Collection;
import cz.mg.collections.array.Array;

import java.util.function.Consumer;

@SuppressWarnings({"unchecked", "rawtypes"})
public @Service class TreeIterator {
    private static volatile @Service TreeIterator instance;

    public static @Service TreeIterator getInstance() {
        if (instance == null) {
            synchronized (Service.class) {
                if (instance == null) {
                    instance = new TreeIterator();
                }
            }
        }
        return instance;
    }

    private TreeIterator() {
    }

    public void forEachFile(
        @Optional Node node,
        @Mandatory Consumer<File> consumer,
        @Mandatory Progress progress,
        @Mandatory String description
    ) {
        forEachMain(toCollection(node), (Consumer) consumer, progress, description, true, false);
    }

    public void forEachDirectory(
        @Optional Node node,
        @Mandatory Consumer<Directory> consumer,
        @Mandatory Progress progress,
        @Mandatory String description
    ) {
        forEachMain(toCollection(node), (Consumer) consumer, progress, description, false, true);
    }

    public void forEachNode(
        @Optional Node node,
        @Mandatory Consumer<Node> consumer,
        @Mandatory Progress progress,
        @Mandatory String description
    ) {
        forEachMain(toCollection(node), consumer, progress, description, true, true);
    }

    public void forEachFile(
        @Mandatory Collection<? extends Node> nodes,
        @Mandatory Consumer<File> consumer,
        @Mandatory Progress progress,
        @Mandatory String description
    ) {
        forEachMain(nodes, (Consumer) consumer, progress, description, true, false);
    }

    public void forEachDirectory(
        @Mandatory Collection<? extends Node> nodes,
        @Mandatory Consumer<Directory> consumer,
        @Mandatory Progress progress,
        @Mandatory String description
    ) {
        forEachMain(nodes, (Consumer) consumer, progress, description, false, true);
    }

    public void forEachNode(
        @Mandatory Collection<? extends Node> nodes,
        @Mandatory Consumer<Node> consumer,
        @Mandatory Progress progress,
        @Mandatory String description
    ) {
        forEachMain(nodes, consumer, progress, description, true, true);
    }

    private @Mandatory Collection<? extends Node> toCollection(@Optional Node node) {
        return node == null ? new Array<>() : new Array<>(node);
    }

    private void forEachMain(
        @Mandatory Collection<? extends Node> nodes,
        @Mandatory Consumer<Node> consumer,
        @Mandatory Progress progress,
        @Mandatory String description,
        boolean files,
        boolean directories
    ) {
        progress.setDescription(description);
        progress.setLimit(estimate(nodes, files, directories));

        for (Node node : nodes) {
            if (node instanceof Directory directory) {
                forEachRecursive(directory, consumer, progress, files, directories);
            } else if (node instanceof File file) {
                if (files) {
                    consumer.accept(file);
                    progress.step();
                }
            }
        }
    }

    private void forEachRecursive(
        @Mandatory Directory directory,
        @Mandatory Consumer<Node> consumer,
        @Mandatory Progress progress,
        boolean files,
        boolean directories
    ) {
        if (directories) {
            consumer.accept(directory);
            progress.step();
        }

        if (files) {
            for (File file : directory.getFiles()) {
                consumer.accept(file);
                progress.step();
            }
        }

        for (Directory subdirectory : directory.getDirectories()) {
            forEachRecursive(subdirectory, consumer, progress, files, directories);
        }
    }

    private long estimate(@Mandatory Collection<? extends Node> nodes, boolean files, boolean directories) {
        long estimate = 0;
        for (Node node : nodes) {
            estimate += estimate(node, files, directories);
        }
        return estimate;
    }

    private long estimate(@Mandatory Node node, boolean files, boolean directories) {
        if (node instanceof Directory directory) {
            if (directory.getProperties() != null) {
                long selfCount = directories ? 1 : 0;
                long fileCount = files ? directory.getProperties().getTotalFileCount() : 0;
                long directoryCount = directories ? directory.getProperties().getTotalDirectoryCount() : 0;
                return selfCount + fileCount + directoryCount;
            } else {
                return 0;
            }
        } else if (node instanceof File) {
            return files ? 1 : 0;
        } else {
            return 0;
        }
    }
}
