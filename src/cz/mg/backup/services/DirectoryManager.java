package cz.mg.backup.services;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.annotations.requirement.Optional;
import cz.mg.backup.components.Progress;
import cz.mg.backup.entities.Checksum;
import cz.mg.backup.entities.Directory;
import cz.mg.collections.map.Map;
import cz.mg.collections.pair.Pair;

import java.nio.file.Path;
import java.util.Date;

public @Service class DirectoryManager {
    private static volatile @Service DirectoryManager instance;

    public static @Service DirectoryManager getInstance() {
        if (instance == null) {
            synchronized (Service.class) {
                if (instance == null) {
                    instance = new DirectoryManager();
                    instance.directoryReader = DirectoryReader.getInstance();
                    instance.statisticsCounter = StatisticsCounter.getInstance();
                    instance.checksumService = ChecksumService.getInstance();
                    instance.directoryComparator = DirectoryComparator.getInstance();
                }
            }
        }
        return instance;
    }

    private @Service DirectoryReader directoryReader;
    private @Service StatisticsCounter statisticsCounter;
    private @Service ChecksumService checksumService;
    private @Service DirectoryComparator directoryComparator;

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
        progress.setLimit(4);

        Map<Path, Pair<Checksum, Date>> checksums = checksumService.collect(
            directory,
            progress.nest("Collect checksums")
        );

        progress.step();

        directory = directoryReader.read(path, progress.nest("Load directory " + path));

        progress.step();

        statisticsCounter.count(directory, progress.nest("Gather statistics"));

        progress.step();

        checksumService.restore(directory, checksums, progress.nest("Restore checksums"));

        progress.step();

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
