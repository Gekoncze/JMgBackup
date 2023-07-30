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

        if (!Objects.equals(first.getSize(), second.getSize())) {
            CompareException exception = new CompareException(
                "Expected size " + first.getSize() + ", but got " + second.getSize() + "."
            );
            first.getErrors().addLast(exception);
            second.getErrors().addLast(exception);
        }

        if (!Objects.equals(first.getHash(), second.getHash())) {
            CompareException exception = new CompareException(
                "Expected hash " + first.getHash() + ", but got " + second.getHash() + "."
            );
            first.getErrors().addLast(exception);
            second.getErrors().addLast(exception);
        }
    }
}
