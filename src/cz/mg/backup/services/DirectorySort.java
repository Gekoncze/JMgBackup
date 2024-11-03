package cz.mg.backup.services;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.backup.components.Progress;
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
                }
            }
        }
        return instance;
    }

    private @Service ListSort listSort;

    private DirectorySort() {
    }

    public void sort(@Mandatory Directory directory, @Mandatory Progress progress) {
        progress.setLimit(estimate(directory));
        listSort.sort(directory.getDirectories(), (n1, n2) -> order(n1, n2, progress), Direction.ASCENDING);
        listSort.sort(directory.getFiles(), (n1, n2) -> order(n1, n2, progress), Direction.ASCENDING);
    }

    private int order(@Mandatory Node n1, @Mandatory Node n2, @Mandatory Progress progress) {
        progress.step();
        String first = n1.getPath().getFileName().toString();
        String second = n2.getPath().getFileName().toString();
        return first.toLowerCase().compareTo(second.toLowerCase());
    }

    private long estimate(@Mandatory Directory directory) {
        return estimate(directory.getDirectories().count()) + estimate(directory.getFiles().count());
    }

    private long estimate(long count) {
        return Math.round(Math.ceil(count * Math.log(count)));
    }
}
