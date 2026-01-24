package cz.mg.backup.services;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.backup.components.Progress;
import cz.mg.backup.entities.Algorithm;
import cz.mg.backup.entities.Node;
import cz.mg.collections.list.List;

public @Service class ChecksumActions {
    private static final String COMPUTE_DESCRIPTION = "Compute checksum";
    private static final String CLEAR_DESCRIPTION = "Clear checksum";

    private static volatile @Service ChecksumActions instance;

    public static @Service ChecksumActions getInstance() {
        if (instance == null) {
            synchronized (Service.class) {
                if (instance == null) {
                    instance = new ChecksumActions();
                    instance.simplifier = Simplifier.getInstance();
                    instance.treeIterator = TreeIterator.getInstance();
                    instance.checksumManager = ChecksumManager.getInstance();
                }
            }
        }
        return instance;
    }

    private @Service Simplifier simplifier;
    private @Service TreeIterator treeIterator;
    private @Service ChecksumManager checksumManager;

    public void compute(@Mandatory List<Node> nodes, @Mandatory Algorithm algorithm, @Mandatory Progress progress) {
        treeIterator.forEachFile(
            simplifier.simplify(nodes, progress.nest()),
            file -> checksumManager.compute(file, algorithm, progress),
            progress,
            COMPUTE_DESCRIPTION
        );
    }

    public void clear(@Mandatory List<Node> nodes, @Mandatory Progress progress) {
        treeIterator.forEachFile(
            simplifier.simplify(nodes, progress.nest()),
            checksumManager::clear,
            progress,
            CLEAR_DESCRIPTION
        );
    }
}
