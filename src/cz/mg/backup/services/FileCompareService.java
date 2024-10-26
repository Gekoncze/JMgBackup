package cz.mg.backup.services;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.backup.entities.File;
import cz.mg.backup.exceptions.CompareException;

import java.util.Objects;

public @Service class FileCompareService {
    private static volatile @Service FileCompareService instance;

    public static @Service FileCompareService getInstance() {
        if (instance == null) {
            synchronized (Service.class) {
                if (instance == null) {
                    instance = new FileCompareService();
                }
            }
        }
        return instance;
    }

    private FileCompareService() {
    }

    public void compare(@Mandatory File first, @Mandatory File second) {
        first.getErrors().removeIf(e -> e instanceof CompareException);
        second.getErrors().removeIf(e -> e instanceof CompareException);

        if (first.getProperties() == null) {
            CompareException exception = new CompareException(
                "Missing file properties."
            );
            first.getErrors().addLast(exception);
        }

        if (second.getProperties() == null) {
            CompareException exception = new CompareException(
                "Missing file properties."
            );
            second.getErrors().addLast(exception);
        }

        if (first.getProperties() != null && second.getProperties() != null) {
            if (!Objects.equals(first.getProperties().getSize(), second.getProperties().getSize())) {
                CompareException exception = new CompareException(
                    "Expected size " + first.getProperties().getSize() + ", " +
                        "but got " + second.getProperties().getSize() + "."
                );
                first.getErrors().addLast(exception);
                second.getErrors().addLast(exception);
            }

            if (!Objects.equals(first.getProperties().getHash(), second.getProperties().getHash())) {
                CompareException exception = new CompareException(
                    "Expected hash " + first.getProperties().getHash() + ", " +
                        "but got " + second.getProperties().getHash() + "."
                );
                first.getErrors().addLast(exception);
                second.getErrors().addLast(exception);
            }
        }
    }
}
