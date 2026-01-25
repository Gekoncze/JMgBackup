package cz.mg.backup.services;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.backup.components.Progress;
import cz.mg.backup.entities.Algorithm;
import cz.mg.backup.entities.Checksum;
import cz.mg.backup.exceptions.CancelException;
import cz.mg.backup.exceptions.FileSystemException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.security.DigestInputStream;
import java.security.MessageDigest;

public @Service class ChecksumReader {
    private static final int BUFFER_SIZE = 1048576 * 2;
    private static final String DESCRIPTION = "Checksum";

    private static volatile @Service ChecksumReader instance;

    public static @Service ChecksumReader getInstance() {
        if (instance == null) {
            synchronized (Service.class) {
                if (instance == null) {
                    instance = new ChecksumReader();
                    instance.hashConverter = HashConverter.getInstance();
                }
            }
        }
        return instance;
    }

    private @Service HashConverter hashConverter;

    private ChecksumReader() {
    }

    public @Mandatory Checksum read(
        @Mandatory Path path,
        @Mandatory Algorithm algorithm,
        @Mandatory Progress progress
    ) {
        try {
            progress.setDescription(DESCRIPTION + " " + path.getFileName());
            progress.setLimit(estimate(path));
            progress.setValue(0L);

            MessageDigest messageDigest = MessageDigest.getInstance(algorithm.getCode());

            try (
                DigestInputStream stream = new DigestInputStream(
                    Files.newInputStream(path, LinkOption.NOFOLLOW_LINKS),
                    messageDigest
                )
            ) {
                byte[] buffer = new byte[BUFFER_SIZE];
                while (stream.read(buffer) > 0) {
                    progress.step();
                }
            }

            return new Checksum(algorithm, hashConverter.convert(messageDigest.digest()));
        } catch (Exception e) {
            if (e instanceof CancelException ce) {
                throw ce;
            } else {
                throw new FileSystemException("Could not compute checksum.", e);
            }
        }
    }

    private long estimate(@Mandatory Path path) throws IOException {
        long size = Files.size(path);
        return size / BUFFER_SIZE + (size % BUFFER_SIZE > 0 ? 1 : 0);
    }
}
