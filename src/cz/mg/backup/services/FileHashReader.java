package cz.mg.backup.services;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.annotations.requirement.Optional;
import cz.mg.backup.entities.Settings;

import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.security.DigestInputStream;
import java.security.MessageDigest;

public @Service class FileHashReader {
    private static final int BUFFER_SIZE = 1048576;

    private static volatile @Service FileHashReader instance;

    public static @Service FileHashReader getInstance() {
        if (instance == null) {
            synchronized (Service.class) {
                if (instance == null) {
                    instance = new FileHashReader();
                    instance.hashConverter = HashConverter.getInstance();
                    instance.taskService = TaskService.getInstance();
                }
            }
        }
        return instance;
    }

    private @Service HashConverter hashConverter;
    private @Service TaskService taskService;

    private FileHashReader() {
    }

    public @Optional String read(@Mandatory Path path, @Mandatory Settings settings) {
        try {
            if (settings.getHashAlgorithm() != null) {
                MessageDigest algorithm = MessageDigest.getInstance(settings.getHashAlgorithm());
                try (
                    DigestInputStream stream = new DigestInputStream(
                        Files.newInputStream(path, LinkOption.NOFOLLOW_LINKS),
                        algorithm
                    )
                ) {
                    byte[] buffer = new byte[BUFFER_SIZE];
                    while (stream.read(buffer) > 0) {
                        taskService.update();
                    }
                }
                return hashConverter.convert(algorithm.digest());
            } else {
                return null;
            }
        } catch (Exception e) {
            throw new RuntimeException("Could not compute file hash.", e);
        }
    }
}
