package cz.mg.backup.services;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.backup.entities.Algorithm;
import cz.mg.backup.entities.Checksum;
import cz.mg.backup.exceptions.StorageException;

import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.security.DigestInputStream;
import java.security.MessageDigest;

public @Service class ChecksumReader {
    private static final int BUFFER_SIZE = 1048576;

    private static volatile @Service ChecksumReader instance;

    public static @Service ChecksumReader getInstance() {
        if (instance == null) {
            synchronized (Service.class) {
                if (instance == null) {
                    instance = new ChecksumReader();
                    instance.hashConverter = HashConverter.getInstance();
                    instance.taskService = TaskService.getInstance();
                }
            }
        }
        return instance;
    }

    private @Service HashConverter hashConverter;
    private @Service TaskService taskService;

    private ChecksumReader() {
    }

    public @Mandatory Checksum read(@Mandatory Path path, @Mandatory Algorithm algorithm) {
        try {
            MessageDigest messageDigest = MessageDigest.getInstance(algorithm.getCode());
            try (
                DigestInputStream stream = new DigestInputStream(
                    Files.newInputStream(path, LinkOption.NOFOLLOW_LINKS),
                    messageDigest
                )
            ) {
                byte[] buffer = new byte[BUFFER_SIZE];
                while (stream.read(buffer) > 0) {
                    taskService.update();
                }
            }
            return new Checksum(hashConverter.convert(messageDigest.digest()));
        } catch (Exception e) {
            throw new StorageException("Could not compute checksum.", e);
        }
    }
}
