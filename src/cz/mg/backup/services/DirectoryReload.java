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

public @Service class DirectoryReload {
    private static volatile @Service DirectoryReload instance;

    public static @Service DirectoryReload getInstance() {
        if (instance == null) {
            synchronized (Service.class) {
                if (instance == null) {
                    instance = new DirectoryReload();
                    instance.directoryReader = DirectoryReader.getInstance();
                    instance.statisticsCounter = StatisticsCounter.getInstance();
                    instance.checksumService = ChecksumService.getInstance();
                }
            }
        }
        return instance;
    }

    private @Service DirectoryReader directoryReader;
    private @Service StatisticsCounter statisticsCounter;
    private @Service ChecksumService checksumService;

    private DirectoryReload() {
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
}
