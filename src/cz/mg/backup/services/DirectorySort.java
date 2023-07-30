package cz.mg.backup.services;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.backup.entities.Directory;
import cz.mg.backup.entities.Node;
import cz.mg.collections.components.Direction;
import cz.mg.collections.services.sort.ListSort;
import cz.mg.collections.services.sort.MergeListSort;

public @Service class DirectorySort {
    private static volatile @Service DirectorySort instance;

    public static @Service DirectorySort getInstance() {
        if (instance == null) {
            synchronized (Service.class) {
                if (instance == null) {
                    instance = new DirectorySort();
                    instance.listSort = MergeListSort.getInstance();
                    instance.taskService = TaskService.getInstance();
                }
            }
        }
        return instance;
    }

    private @Service ListSort listSort;
    private @Service TaskService taskService;

    private DirectorySort() {
    }

    public void sort(@Mandatory Directory directory) {
        listSort.sort(directory.getDirectories(), this::order, Direction.ASCENDING);
        listSort.sort(directory.getFiles(), this::order, Direction.ASCENDING);
    }

    private int order(@Mandatory Node n1, @Mandatory Node n2) {
        taskService.update();
        String first = n1.getPath().getFileName().toString();
        String second = n2.getPath().getFileName().toString();
        return first.toLowerCase().compareTo(second.toLowerCase());
    }
}
