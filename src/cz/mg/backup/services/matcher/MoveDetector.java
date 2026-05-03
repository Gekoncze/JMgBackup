package cz.mg.backup.services.matcher;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.requirement.Mandatory;
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
 * Found moved files are marked with an exception containing suspected file.
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
        progress.setValue(0L);

        for (ReadablePair<Key, List<File>> pair : sourceMap) {
            Key key = pair.getKey();
            List<File> sourceFiles = pair.getValue();
            List<File> targetFiles = targetMap.getOptional(key);

            // currently only finds moves for non-duplicated files
            // this requirement could be loosen if needed
            if (
                sourceFiles != null &&
                targetFiles != null &&
                sourceFiles.count() == 1 &&
                targetFiles.count() == 1
            ) {
                File source = sourceFiles.getFirst();
                File target = targetFiles.getFirst();

                if (moved(source, target)) {
                    if (source.getException() == null) {
                        source.setException(new MoveException(target));
                    }

                    if (target.getException() == null) {
                        target.setException(new MoveException(source));
                    }
                }
            }

            progress.step();
        }
    }

    private boolean moved(@Mandatory File source, @Mandatory File target) {
        // root name may differ, so removing it for comparison
        // could be more strict here, but then would need to be consistent across app
        return !Objects.equals(
            pathService.removeLeadingPart(source.getRelativePath()),
            pathService.removeLeadingPart(target.getRelativePath())
        );
    }
}
