package cz.mg.backup.services;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.annotations.requirement.Optional;
import cz.mg.backup.components.Progress;
import cz.mg.backup.entities.Directory;
import cz.mg.backup.entities.Node;

import java.nio.file.Path;
import java.util.Objects;

public @Service class DirectorySearch {
    private static volatile @Service DirectorySearch instance;

    public static @Service DirectorySearch getInstance() {
        if (instance == null) {
            synchronized (Service.class) {
                if (instance == null) {
                    instance = new DirectorySearch();
                    instance.treeIterator = TreeIterator.getInstance();
                }
            }
        }
        return instance;
    }

    private @Service TreeIterator treeIterator;

    private DirectorySearch() {
    }

    public @Optional Node find(@Optional Directory directory, @Mandatory Path path, @Mandatory Progress progress) {
        Node[] wanted = new Node[1];
        treeIterator.forEachNode(directory, node -> {
            if (Objects.equals(node.getPath(), path)) {
                wanted[0] = node;
            }
        }, progress);
        return wanted[0];
    }
}
