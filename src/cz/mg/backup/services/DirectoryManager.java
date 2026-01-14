package cz.mg.backup.services;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.annotations.requirement.Optional;
import cz.mg.backup.components.Progress;
import cz.mg.backup.entities.Directory;

import java.nio.file.Path;

public @Service class DirectoryManager {
    private static volatile @Service DirectoryManager instance;

    public static @Service DirectoryManager getInstance() {
        if (instance == null) {
            synchronized (Service.class) {
                if (instance == null) {
                    instance = new DirectoryManager();
                    instance.directoryReader = DirectoryReader.getInstance();
                    instance.statisticsCounter = StatisticsCounter.getInstance();
                    instance.checksumManager = ChecksumManager.getInstance();
                    instance.directoryComparator = DirectoryComparator.getInstance();
                    instance.pathConverter = PathConverter.getInstance();
                }
            }
        }
        return instance;
    }

    private @Service DirectoryReader directoryReader;
    private @Service StatisticsCounter statisticsCounter;
    private @Service ChecksumManager checksumManager;
    private @Service DirectoryComparator directoryComparator;
    private @Service PathConverter pathConverter;

    private DirectoryManager() {
    }

    /**
     * Reloads given directory.
     * Checksums are retained where possible.
     */
    public @Mandatory Directory reload(
        @Optional Directory directory,
        @Mandatory Path path,
        @Mandatory Progress progress
    ) {
        progress.setLimit(5);

        var checksums = checksumManager.collect(directory, progress.nest("Collect checksums"));
        progress.step(); // 1

        directory = directoryReader.read(path, progress.nest("Load directory " + path));
        progress.step(); // 2

        pathConverter.computeRelativePaths(directory, progress.nest("Calculate relative path"));
        progress.step(); // 3

        statisticsCounter.count(directory, progress.nest("Gather statistics"));
        progress.step(); // 4

        checksumManager.restore(directory, checksums, progress.nest("Restore checksums"));
        progress.step(); // 5

        return directory;
    }

    /**
     * Compares given directories.
     * Statistics are updated afterward.
     */
    public void compare(
        @Optional Directory a,
        @Optional Directory b,
        @Mandatory Progress progress
    ) {
        progress.setLimit(3);

        if (a != null && b != null) {
            directoryComparator.compare(a, b, progress.nest("Compare"));
        } else if (a != null) {
            directoryComparator.compare(a, a, progress.nest("Compare"));
        } else if (b != null) {
            directoryComparator.compare(b, b, progress.nest("Compare"));
        }

        progress.step();

        statisticsCounter.count(a, progress.nest("Gather statistics"));

        progress.step();

        statisticsCounter.count(b, progress.nest("Gather statistics"));

        progress.step();
    }
}
