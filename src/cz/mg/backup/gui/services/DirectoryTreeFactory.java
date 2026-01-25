package cz.mg.backup.gui.services;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.backup.components.Progress;
import cz.mg.backup.entities.Directory;
import cz.mg.backup.entities.File;
import cz.mg.backup.entities.Node;
import cz.mg.backup.gui.views.directory.ObjectTreeEntry;
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

    public @Mandatory ObjectTreeEntry create(@Mandatory Directory directory, @Mandatory Progress progress) {
        progress.setDescription(DESCRIPTION);
        progress.setLimit(estimate(directory));
        progress.setValue(0L);

        ObjectTreeEntry entry = create(directory, 0, progress);
        progress.step();
        return entry;
    }

    private @Mandatory ObjectTreeEntry create(@Mandatory Directory directory, int index, @Mandatory Progress progress) {
        Array<ObjectTreeEntry> children = new Array<>(
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

        return new ObjectTreeEntry(directory, getName(directory), index, false, children, this::compare, this::hash);
    }

    private @Mandatory ObjectTreeEntry create(@Mandatory File file, int index) {
        return new ObjectTreeEntry(file, getName(file), index, true, null, this::compare, this::hash);
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
