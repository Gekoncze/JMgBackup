package cz.mg.backup.services;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.backup.components.Progress;
import cz.mg.backup.entities.Algorithm;
import cz.mg.backup.entities.Checksum;
import cz.mg.backup.exceptions.FileSystemException;

import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.BasicFileAttributeView;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.util.Objects;

public @Service class FileCopy {
    private static final long BUFFER_SIZE = 1000 * 1000 * 10;
    private static final String COPY_DESCRIPTION = "Copy file";

    private static volatile @Service FileCopy instance;

    public static @Service FileCopy getInstance() {
        if (instance == null) {
            synchronized (Service.class) {
                if (instance == null) {
                    instance = new FileCopy();
                    instance.checksumReader = ChecksumReader.getInstance();
                }
            }
        }
        return instance;
    }

    private @Service ChecksumReader checksumReader;

    private FileCopy() {
    }

    public void copy(
        @Mandatory Path source,
        @Mandatory Path target,
        @Mandatory Algorithm algorithm,
        @Mandatory Progress progress
    ) {
        progress.setDescription(COPY_DESCRIPTION);
        progress.setLimit(7L);
        progress.setValue(0L);

        validateParameters(source, target);
        progress.step(); // 1

        createMissingDirectories(target);
        progress.step(); // 2

        Checksum sourceChecksum = checksumReader.read(source, algorithm, progress.nest());
        progress.step(); // 3
        progress.unnest();

        copySourceToTarget(source, target, progress.nest());
        progress.step(); // 4
        progress.unnest();

        copySourceAttributesToTarget(source, target);
        progress.step(); // 5

        Checksum targetChecksum = checksumReader.read(target, algorithm, progress.nest());
        progress.step(); // 6
        progress.unnest();

        validateChecksums(sourceChecksum, targetChecksum);
        progress.step(); // 7
    }

    private void validateParameters(@Mandatory Path source, @Mandatory Path target) {
        if (!Files.isRegularFile(source)) {
            throw new IllegalArgumentException("Expected regular file for source path '" + source + "'.");
        }

        if (Files.exists(target)) {
            throw new IllegalArgumentException("Expected missing file for target path '" + target + "'.");
        }
    }

    private void createMissingDirectories(@Mandatory Path path) {
        try {
            if (path.getParent() != null) {
                Files.createDirectories(path.getParent());
            }
        } catch (IOException e) {
            throw new FileSystemException("Could not create missing directories for '" + path + "'.");
        }
    }

    private void copySourceToTarget(@Mandatory Path source, @Mandatory Path target, @Mandatory Progress progress) {
        try (
            FileChannel sourceChannel = FileChannel.open(source, StandardOpenOption.READ);
            FileChannel targetChannel = FileChannel.open(target, StandardOpenOption.WRITE, StandardOpenOption.CREATE)
        ) {
            long size = getFileSize(source);
            if (size > 0) {
                progress.setDescription(COPY_DESCRIPTION);
                progress.setLimit(Math.max(1, size / BUFFER_SIZE));
                long position = 0;
                while (position < size) {
                    long transferred = sourceChannel.transferTo(position, BUFFER_SIZE, targetChannel);
                    position += transferred;
                    progress.step();
                    if (transferred == 0) break;
                }
            }
        } catch (IOException e) {
            throw new FileSystemException("Could not copy data from '" + source + "' to '" + target + "'.", e);
        }
    }

    private void copySourceAttributesToTarget(@Mandatory Path source, @Mandatory Path target) {
        try {
            BasicFileAttributes sourceAttributes = Files.readAttributes(source, BasicFileAttributes.class);
            FileTime created = sourceAttributes.creationTime();
            FileTime modified = sourceAttributes.lastModifiedTime();
            FileTime accessed = sourceAttributes.lastAccessTime();

            BasicFileAttributeView view = Files.getFileAttributeView(target, BasicFileAttributeView.class);
            view.setTimes(modified, accessed, created);
        } catch (IOException e) {
            throw new FileSystemException("Could not copy attributes from '" + source + "' to '" + target + "'.", e);
        }
    }

    private long getFileSize(@Mandatory Path path) {
        try {
            return Files.size(path);
        } catch (IOException e) {
            throw new FileSystemException("Could not get file size.");
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
