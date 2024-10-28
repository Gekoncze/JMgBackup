package cz.mg.backup.services;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.backup.entities.File;
import cz.mg.backup.exceptions.CompareException;

import java.util.Objects;

public @Service class FileComparator {
    private static volatile @Service FileComparator instance;

    public static @Service FileComparator getInstance() {
        if (instance == null) {
            synchronized (Service.class) {
                if (instance == null) {
                    instance = new FileComparator();
                }
            }
        }
        return instance;
    }

    private FileComparator() {
    }

    public void compare(@Mandatory File first, @Mandatory File second) {
        first.getErrors().removeIf(e -> e instanceof CompareException);
        second.getErrors().removeIf(e -> e instanceof CompareException);

        if (!Objects.equals(first.getProperties().getSize(), second.getProperties().getSize())) {
            CompareException exception = new CompareException(
                "Expected size " + first.getProperties().getSize() + ", " +
                    "but got " + second.getProperties().getSize() + "."
            );
            first.getErrors().addLast(exception);
            second.getErrors().addLast(exception);
        }

        if (first.getChecksum() != null && second.getChecksum() != null) {
            if (!Objects.equals(first.getChecksum().getHash(), second.getChecksum().getHash())) {
                CompareException exception = new CompareException(
                    "Expected hash " + first.getChecksum().getHash() + ", " +
                        "but got " + second.getChecksum().getHash() + "."
                );
                first.getErrors().addLast(exception);
                second.getErrors().addLast(exception);
            }
        }
    }
}
