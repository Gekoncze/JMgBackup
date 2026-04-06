package cz.mg.backup.services.matcher;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.annotations.requirement.Optional;
import cz.mg.backup.components.Progress;
import cz.mg.backup.entities.File;
import cz.mg.backup.exceptions.MoveException;
import cz.mg.backup.services.PathService;
import cz.mg.collections.list.List;
import cz.mg.collections.map.Map;
import cz.mg.collections.pair.ReadablePair;

import java.util.Objects;

/**
 * Class to find files moved in another directory tree.
 * Found moved files are marked with an error containing suspected file.
 */
public @Service class MoveDetector {
    private static final @Mandatory String DESCRIPTION = "Find moved files";
    private static volatile @Service MoveDetector instance;

    public static @Service MoveDetector getInstance() {
        if (instance == null) {
            synchronized (Service.class) {
                if (instance == null) {
                    instance = new MoveDetector();
                    instance.pathService = PathService.getInstance();
                }
            }
        }
        return instance;
    }

    private @Service PathService pathService;

    private MoveDetector() {
    }

    public void findMoves(
        @Mandatory Map<Key, List<File>> sourceMap,
        @Mandatory Map<Key, List<File>> targetMap,
        @Mandatory Progress progress
    ) {
        progress.setDescription(DESCRIPTION);
        progress.setLimit(sourceMap.count());
        progress.setLimit(0L);

        for (ReadablePair<Key, List<File>> pair : sourceMap) {
            // currently only finds moves for non-duplicated files
            if (pair.getValue().count() == 1) {
                Key key = pair.getKey();
                File file = pair.getValue().getFirst();
                if (file.getError() == null) {
                    File suspect = findMatch(key, targetMap);
                    if (suspect != null && moved(file, suspect)) {
                        file.setError(new MoveException(suspect));
                    }
                }
            }
            progress.step();
        }
    }

    private @Optional File findMatch(@Mandatory Key key, @Mandatory Map<Key, List<File>> map) {
        List<File> suspects = map.getOptional(key);
        // currently only finds moves for non-duplicated files
        if (suspects != null && suspects.count() == 1) {
            return suspects.getFirst();
        }
        return null;
    }

    private boolean moved(@Mandatory File source, @Mandatory File target) {
        return !Objects.equals(
            pathService.removeLeadingPart(source.getRelativePath()),
            pathService.removeLeadingPart(target.getRelativePath())
        );
    }
}
