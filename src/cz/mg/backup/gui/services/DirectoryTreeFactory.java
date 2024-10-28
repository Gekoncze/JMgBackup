package cz.mg.backup.gui.services;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.backup.entities.Directory;
import cz.mg.backup.entities.File;
import cz.mg.backup.entities.Node;
import cz.mg.backup.gui.views.directory.ObjectTreeEntry;
import cz.mg.backup.services.TaskService;
import cz.mg.collections.array.Array;

public @Service class DirectoryTreeFactory {
    private static volatile @Service DirectoryTreeFactory instance;

    public static @Service DirectoryTreeFactory getInstance() {
        if (instance == null) {
            synchronized (Service.class) {
                if (instance == null) {
                    instance = new DirectoryTreeFactory();
                    instance.taskService = TaskService.getInstance();
                }
            }
        }
        return instance;
    }

    private @Service TaskService taskService;

    private DirectoryTreeFactory() {
    }

    public @Mandatory ObjectTreeEntry create(@Mandatory Directory directory) {
        return create(directory, 0);
    }

    private @Mandatory ObjectTreeEntry create(@Mandatory Directory directory, int index) {
        Array<ObjectTreeEntry> children = new Array<>(
            directory.getDirectories().count() + directory.getFiles().count()
        );

        int i = 0;

        for (Directory child : directory.getDirectories()) {
            taskService.update();
            children.set(i, create(child, i));
            i++;
        }

        for (File child : directory.getFiles()) {
            taskService.update();
            children.set(i, create(child, i));
            i++;
        }

        return new ObjectTreeEntry(directory, getName(directory), index, false, children);
    }

    private @Mandatory ObjectTreeEntry create(@Mandatory File file, int index) {
        return new ObjectTreeEntry(file, getName(file), index, true, null);
    }

    private @Mandatory String getName(@Mandatory Node node) {
        return node.getPath().getFileName().toString();
    }
}
