package cz.mg.backup.services.detector;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.annotations.requirement.Optional;
import cz.mg.backup.components.Progress;
import cz.mg.backup.entities.Directory;
import cz.mg.backup.entities.File;
import cz.mg.backup.exceptions.DuplicateException;
import cz.mg.backup.exceptions.MoveException;
import cz.mg.backup.services.PathService;
import cz.mg.backup.services.TreeIterator;
import cz.mg.collections.list.List;
import cz.mg.collections.map.Map;
import cz.mg.collections.pair.ReadablePair;

import java.util.Objects;

/**
 * Class to find files that may be duplicates or files moved in the other directory.
 * For precise duplicate detection, it is necessary to do byte to byte comparison on found files.
 */
public @Service class DuplicateDetector {
    private static final String DESCRIPTION = "Find duplicates";
    private static final int PHASES = 6;

    private static volatile @Service DuplicateDetector instance;

    public static @Service DuplicateDetector getInstance() {
        if (instance == null) {
            synchronized (Service.class) {
                if (instance == null) {
                    instance = new DuplicateDetector();
                    instance.iterator = TreeIterator.getInstance();
                    instance.pathService = PathService.getInstance();
                }
            }
        }
        return instance;
    }

    private @Service TreeIterator iterator;
    private @Service PathService pathService;

    private DuplicateDetector() {
    }

    public void findDuplicates(
        @Optional Directory left,
        @Optional Directory right,
        @Mandatory Converter converter,
        @Mandatory Progress progress
    ) {
        Map<Key, List<File>> leftMap = processFiles(left, converter, progress, 1);
        Map<Key, List<File>> rightMap = processFiles(right, converter, progress, 2);
        findDuplicates(leftMap, initProgress(left, progress, 3));
        findDuplicates(rightMap, initProgress(right, progress, 4));
        findMoves(leftMap, rightMap, initProgress(left, progress, 5));
        findMoves(rightMap, leftMap, initProgress(right, progress, 6));
    }

    private @Mandatory Map<Key, List<File>> processFiles(
        @Optional Directory directory,
        @Mandatory Converter converter,
        @Mandatory Progress progress,
        int phase
    ) {
        Map<Key, List<File>> map = createMap();

        iterator.forEachFile(
            directory,
            file -> processFile(file, converter, map),
            progress,
            DESCRIPTION + " " + phase + " / " + PHASES
        );

        return map;
    }

    private void processFile(
        @Mandatory File file,
        @Mandatory Converter converter,
        @Mandatory Map<Key, List<File>> map
    ) {
        Key key = converter.convert(file);
        List<File> suspects = map.getOrCreate(key, List::new);
        suspects.addLast(file);
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
            if (pair.getValue().count() == 1) {
                Key key = pair.getKey();
                File file = pair.getValue().getFirst();
                if (file.getError() == null) {
                    File suspect = findMove(key, targetMap);
                    if (suspect != null && !Objects.equals(
                        pathService.removeLeadingPart(file.getRelativePath()),
                        pathService.removeLeadingPart(suspect.getRelativePath())
                    )) {
                        file.setError(new MoveException(suspect));
                    }
                }
            }
            progress.step();
        }
    }

    private @Optional File findMove(@Mandatory Key key, @Mandatory Map<Key, List<File>> map) {
        List<File> suspects = map.getOptional(key);
        if (suspects != null && suspects.count() == 1) {
            return suspects.getFirst();
        }
        return null;
    }

    private @Mandatory Map<Key, List<File>> createMap() {
        KeyComparator comparator = new KeyComparator();
        Map<Key, List<File>> map = new Map<>(comparator, comparator);
        return map;
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
