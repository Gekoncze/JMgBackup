package cz.mg.backup.services.matcher;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.backup.components.Progress;
import cz.mg.backup.entities.File;
import cz.mg.backup.exceptions.DuplicateException;
import cz.mg.collections.list.List;
import cz.mg.collections.map.Map;
import cz.mg.collections.pair.ReadablePair;

/**
 * Class to find duplicates in grouped files.
 * Found duplicates are marked with an exception containing suspected files.
 */
public @Service class DuplicateDetector {
    private static final @Mandatory String DESCRIPTION = "Find duplicates";

    private static volatile @Service DuplicateDetector instance;

    public static @Service DuplicateDetector getInstance() {
        if (instance == null) {
            synchronized (Service.class) {
                if (instance == null) {
                    instance = new DuplicateDetector();
                }
            }
        }
        return instance;
    }

    private DuplicateDetector() {
    }

    public void findDuplicates(@Mandatory Map<Key, List<File>> map, @Mandatory Progress progress) {
        progress.setDescription(DESCRIPTION);
        progress.setLimit(map.count());
        progress.setValue(0L);

        for (ReadablePair<Key, List<File>> pair : map) {
            List<File> suspects = pair.getValue();
            if (suspects.count() > 1) {
                DuplicateException exception = new DuplicateException(suspects);
                for (File file : pair.getValue()) {
                    if (file.getException() == null) {
                        file.setException(exception);
                    }
                }
            }
            progress.step();
        }
    }
}
