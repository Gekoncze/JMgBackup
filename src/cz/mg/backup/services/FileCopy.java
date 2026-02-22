package cz.mg.backup.services;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.backup.components.Progress;
import cz.mg.backup.exceptions.FileSystemException;

import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.BasicFileAttributeView;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;

public @Service class FileCopy {
    private static final long BUFFER_SIZE = 1000 * 1000 * 10;
    private static final String DESCRIPTION = "Copy file";

    private static volatile @Service FileCopy instance;

    public static @Service FileCopy getInstance() {
        if (instance == null) {
            synchronized (Service.class) {
                if (instance == null) {
                    instance = new FileCopy();
                }
            }
        }
        return instance;
    }

    private FileCopy() {
    }

    public void copy(@Mandatory Path source, @Mandatory Path target, @Mandatory Progress progress) {
        try (
            FileChannel sourceChannel = FileChannel.open(source, StandardOpenOption.READ);
            FileChannel targetChannel = FileChannel.open(target, StandardOpenOption.WRITE, StandardOpenOption.CREATE_NEW)
        ) {
            long size = getFileSize(source);
            progress.setDescription(DESCRIPTION);
            progress.setLimit(Math.max(1L, size / BUFFER_SIZE) + 1L);
            progress.setValue(0L);

            long position = 0;
            while (position < size) {
                long transferred = sourceChannel.transferTo(position, BUFFER_SIZE, targetChannel);
                position += transferred;
                progress.step();
                if (transferred == 0) break;
            }
        } catch (IOException e) {
            throw new FileSystemException("Could not copy data from '" + source + "' to '" + target + "'.", e);
        }

        copyAttributes(source, target);
        progress.step();
    }

    private void copyAttributes(@Mandatory Path source, @Mandatory Path target) {
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
}
