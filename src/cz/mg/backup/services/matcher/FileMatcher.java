package cz.mg.backup.services.matcher;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.annotations.requirement.Optional;
import cz.mg.backup.components.Progress;
import cz.mg.backup.entities.Directory;
import cz.mg.backup.entities.File;
import cz.mg.backup.exceptions.DuplicateException;
import cz.mg.backup.exceptions.MoveException;
import cz.mg.backup.services.PathService;
import cz.mg.collections.list.List;
import cz.mg.collections.map.Map;
import cz.mg.collections.pair.ReadablePair;

import java.util.Objects;

/**
 * Class to find files that may be duplicates or files moved in the other directory.
 * For precise duplicate detection, it is necessary to do byte to byte comparison on files found by this class.
 */
public @Service class FileMatcher {
    private static final String DESCRIPTION = "Find duplicates";
    static final int PHASES = 6;

    private static volatile @Service FileMatcher instance;

    public static @Service FileMatcher getInstance() {
        if (instance == null) {
            synchronized (Service.class) {
                if (instance == null) {
                    instance = new FileMatcher();
                    instance.grouper = Grouper.getInstance();
                    instance.pathService = PathService.getInstance();
                }
            }
        }
        return instance;
    }

    private @Service Grouper grouper;
    private @Service PathService pathService;

    private FileMatcher() {
    }

    public void findDuplicates(
        @Optional Directory left,
        @Optional Directory right,
        @Mandatory Converter converter,
        @Mandatory Progress progress
    ) {
        Map<Key, List<File>> leftMap = grouper.groupFiles(left, converter, progress, 1);
        Map<Key, List<File>> rightMap = grouper.groupFiles(right, converter, progress, 2);
        findDuplicates(leftMap, initProgress(left, progress, 3));
        findDuplicates(rightMap, initProgress(right, progress, 4));
        findMoves(leftMap, rightMap, initProgress(left, progress, 5));
        findMoves(rightMap, leftMap, initProgress(right, progress, 6));
    }

    private void findDuplicates(@Mandatory Map<Key, List<File>> map, @Mandatory Progress progress) {
        for (ReadablePair<Key, List<File>> pair : map) {
            List<File> suspects = pair.getValue();
            if (suspects.count() > 1) {
                DuplicateException error = new DuplicateException(suspects);
                for (File file : pair.getValue()) {
                    if (file.getError() == null) {
                        file.setError(error);
                    }
                    progress.step();
                }
            } else {
                progress.step();
            }
        }
    }

    private void findMoves(
        @Mandatory Map<Key, List<File>> sourceMap,
        @Mandatory Map<Key, List<File>> targetMap,
        @Mandatory Progress progress
    ) {
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

    private @Mandatory Progress initProgress(
        @Optional Directory directory,
        @Mandatory Progress progress,
        int phase
    ) {
        progress.setDescription(DESCRIPTION + " " + phase + " / " + PHASES);
        progress.setLimit(directory != null ? directory.getProperties().getTotalFileCount() : 0L);
        progress.setValue(0L);
        return progress;
    }
}
