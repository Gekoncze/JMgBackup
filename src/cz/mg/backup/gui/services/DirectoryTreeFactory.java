package cz.mg.backup.gui.services;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.backup.components.Progress;
import cz.mg.backup.entities.Directory;
import cz.mg.backup.entities.File;
import cz.mg.backup.entities.Node;
import cz.mg.backup.gui.views.directory.DirectoryTreeEntry;
import cz.mg.collections.array.Array;

import java.util.Objects;

public @Service class DirectoryTreeFactory {
    private static final String DESCRIPTION = "Build directory tree";

    private static volatile @Service DirectoryTreeFactory instance;

    public static @Service DirectoryTreeFactory getInstance() {
        if (instance == null) {
            synchronized (Service.class) {
                if (instance == null) {
                    instance = new DirectoryTreeFactory();
                }
            }
        }
        return instance;
    }

    private DirectoryTreeFactory() {
    }

    public @Mandatory DirectoryTreeEntry create(@Mandatory Directory directory, @Mandatory Progress progress) {
        progress.setDescription(DESCRIPTION);
        progress.setLimit(estimate(directory));
        progress.setValue(0L);

        DirectoryTreeEntry entry = create(directory, 0, progress);
        progress.step();
        return entry;
    }

    private @Mandatory DirectoryTreeEntry create(@Mandatory Directory directory, int index, @Mandatory Progress progress) {
        Array<DirectoryTreeEntry> children = new Array<>(
            directory.getDirectories().count() + directory.getFiles().count()
        );

        int i = 0;

        for (Directory child : directory.getDirectories()) {
            children.set(i, create(child, i, progress));
            progress.step();
            i++;
        }

        for (File child : directory.getFiles()) {
            children.set(i, create(child, i));
            progress.step();
            i++;
        }

        return new DirectoryTreeEntry(
            directory,
            getName(directory),
            index,
            false,
            children,
            this::compare,
            this::hash
        );
    }

    private @Mandatory DirectoryTreeEntry create(@Mandatory File file, int index) {
        return new DirectoryTreeEntry(
            file,
            getName(file),
            index,
            true,
            new Array<>(),
            this::compare,
            this::hash
        );
    }

    private @Mandatory String getName(@Mandatory Node node) {
        return node.getPath().getFileName().toString();
    }

    private boolean compare(@Mandatory Node a, @Mandatory Node b) {
        return Objects.equals(a.getPath(), b.getPath());
    }

    private int hash(@Mandatory Node n) {
        return Objects.hash(n.getPath());
    }

    private long estimate(@Mandatory Directory directory) {
        return directory.getProperties().getTotalCount() + 1L;
    }
}
