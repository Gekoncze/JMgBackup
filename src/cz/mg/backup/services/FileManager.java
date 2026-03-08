package cz.mg.backup.services;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.backup.components.Progress;
import cz.mg.backup.entities.Algorithm;
import cz.mg.backup.entities.Checksum;
import cz.mg.backup.exceptions.FileSystemException;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

public @Service class FileManager {
    private static volatile @Service FileManager instance;

    public static @Service FileManager getInstance() {
        if (instance == null) {
            synchronized (Service.class) {
                if (instance == null) {
                    instance = new FileManager();
                    instance.directoryWriter = DirectoryWriter.getInstance();
                    instance.checksumReader = ChecksumReader.getInstance();
                    instance.fileCopy = FileCopy.getInstance();
                }
            }
        }
        return instance;
    }

    private @Service DirectoryWriter directoryWriter;
    private @Service ChecksumReader checksumReader;
    private @Service FileCopy fileCopy;

    private FileManager() {
    }

    public void copy(
        @Mandatory Path source,
        @Mandatory Path target,
        @Mandatory Algorithm algorithm,
        @Mandatory Progress progress
    ) {
        validateParameters(source, target);
        Checksum sourceChecksum = checksumReader.read(source, algorithm, progress);
        directoryWriter.createDirectories(target.getParent());
        fileCopy.copy(source, target, progress);
        Checksum targetChecksum = checksumReader.read(target, algorithm, progress);
        validateChecksums(sourceChecksum, targetChecksum);
    }

    private void validateParameters(@Mandatory Path source, @Mandatory Path target) {
        if (!Files.isRegularFile(source)) {
            throw new IllegalArgumentException("Expected regular file for source path '" + source + "'.");
        }

        if (Files.exists(target)) {
            throw new IllegalArgumentException("Expected missing file for target path '" + target + "'.");
        }
    }

    private void validateChecksums(@Mandatory Checksum sourceChecksum, @Mandatory Checksum targetChecksum) {
        if (!Objects.equals(sourceChecksum.getHash(), targetChecksum.getHash())) {
            throw new FileSystemException(
                "Copy operation failed. Checksum does not match: "
                    + sourceChecksum.getHash() + " vs " + targetChecksum.getHash()
            );
        }
    }
}
