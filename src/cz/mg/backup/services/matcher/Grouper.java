package cz.mg.backup.services.matcher;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.annotations.requirement.Optional;
import cz.mg.backup.components.Progress;
import cz.mg.backup.entities.Directory;
import cz.mg.backup.entities.File;
import cz.mg.backup.services.TreeIterator;
import cz.mg.collections.list.List;
import cz.mg.collections.map.Map;

import static cz.mg.backup.services.matcher.FileMatcher.PHASES;

/**
 * Class to group files by keys.
 */
public class Grouper {
    private static final String DESCRIPTION = "Group files";

    private static volatile @Service Grouper instance;

    public static @Service Grouper getInstance() {
        if (instance == null) {
            synchronized (Service.class) {
                if (instance == null) {
                    instance = new Grouper();
                    instance.iterator = TreeIterator.getInstance();
                }
            }
        }
        return instance;
    }

    private @Service TreeIterator iterator;

    private Grouper() {
    }

    /**
     * Group files from given directory tree by keys produced by given converter.
     */
    public @Mandatory Map<Key, List<File>> groupFiles(
        @Optional Directory directory,
        @Mandatory Converter converter,
        @Mandatory Progress progress,
        int phase
    ) {
        Map<Key, List<File>> map = createMap();

        iterator.forEachFile(
            directory,
            file -> groupFile(file, converter, map),
            progress,
            DESCRIPTION + " " + phase + " / " + PHASES
        );

        return map;
    }

    private void groupFile(
        @Mandatory File file,
        @Mandatory Converter converter,
        @Mandatory Map<Key, List<File>> map
    ) {
        Key key = converter.convert(file);
        List<File> suspects = map.getOrCreate(key, List::new);
        suspects.addLast(file);
    }

    private @Mandatory Map<Key, List<File>> createMap() {
        KeyComparator comparator = new KeyComparator();
        Map<Key, List<File>> map = new Map<>(comparator, comparator);
        return map;
    }
}
