package cz.mg.backup.services.matcher;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.annotations.requirement.Optional;
import cz.mg.backup.components.Progress;
import cz.mg.backup.entities.Directory;
import cz.mg.backup.entities.File;
import cz.mg.collections.list.List;
import cz.mg.collections.map.Map;

/**
 * Class to match files to find duplicates and moved files.
 * For precise detection, it is necessary to do byte to byte comparison as a next step on files found by this class.
 */
public @Service class FileMatcher {
    private static volatile @Service FileMatcher instance;

    public static @Service FileMatcher getInstance() {
        if (instance == null) {
            synchronized (Service.class) {
                if (instance == null) {
                    instance = new FileMatcher();
                    instance.fileGrouper = FileGrouper.getInstance();
                    instance.duplicateDetector = DuplicateDetector.getInstance();
                    instance.moveDetector = MoveDetector.getInstance();
                }
            }
        }
        return instance;
    }

    private @Service FileGrouper fileGrouper;
    private @Service DuplicateDetector duplicateDetector;
    private @Service MoveDetector moveDetector;

    private FileMatcher() {
    }

    public void match(
        @Optional Directory left,
        @Optional Directory right,
        @Mandatory Converter converter,
        @Mandatory Progress progress
    ) {
        Map<Key, List<File>> leftMap = fileGrouper.groupFiles(left, converter, progress);
        Map<Key, List<File>> rightMap = fileGrouper.groupFiles(right, converter, progress);
        duplicateDetector.findDuplicates(leftMap, progress);
        duplicateDetector.findDuplicates(rightMap, progress);
        moveDetector.findMoves(leftMap, rightMap, progress);
        moveDetector.findMoves(rightMap, leftMap, progress);
    }
}
