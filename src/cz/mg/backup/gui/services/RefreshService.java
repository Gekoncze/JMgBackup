package cz.mg.backup.gui.services;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.annotations.requirement.Optional;
import cz.mg.backup.components.Progress;
import cz.mg.backup.entities.Node;
import cz.mg.backup.gui.entities.State;
import cz.mg.backup.services.DirectorySearch;
import cz.mg.backup.services.comparator.DirectoryComparator;

import java.nio.file.Path;

/**
 * Refresh service to be called after anything changed in left or right directory.
 */
public @Service class RefreshService {
    private static volatile @Service RefreshService instance;

    public static @Service RefreshService getInstance() {
        if (instance == null) {
            synchronized (Service.class) {
                if (instance == null) {
                    instance = new RefreshService();
                    instance.comparator = DirectoryComparator.getInstance();
                    instance.search = DirectorySearch.getInstance();
                }
            }
        }
        return instance;
    }

    private @Service DirectoryComparator comparator;
    private @Service DirectorySearch search;

    private RefreshService() {
    }

    public void refresh(@Mandatory State state, @Mandatory Progress progress) {
        comparator.compare(state.getLeft(), state.getRight(), progress);
        state.setDetails(search.find(state.getLeft(), state.getRight(), getPath(state.getDetails()), progress));
    }

    private @Optional Path getPath(@Optional Node node) {
        return node != null ? node.getPath() : null;
    }
}
