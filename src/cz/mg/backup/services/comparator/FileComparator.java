package cz.mg.backup.services.comparator;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.backup.entities.File;
import cz.mg.backup.exceptions.CompareException;
import cz.mg.backup.exceptions.MismatchException;

import java.util.Objects;

public @Service class FileComparator extends NodeComparator {
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

    /**
     * Compares given files and stores compare exceptions in them.
     */
    public void compare(@Mandatory File first, @Mandatory File second) {
        clearCompareError(first);
        clearCompareError(second);

        if (!Objects.equals(first.getProperties().getSize(), second.getProperties().getSize())) {
            CompareException exception = new MismatchException(
                "Expected size " + first.getProperties().getSize() + ", " +
                    "but got " + second.getProperties().getSize() + "."
            );
            setCompareError(first, exception);
            setCompareError(second, exception);
        }

        if (first.getChecksum() != null && second.getChecksum() != null) {
            if (!Objects.equals(first.getChecksum().getAlgorithm(), second.getChecksum().getAlgorithm())) {
                CompareException exception = new MismatchException(
                    "Expected algorithm " + first.getChecksum().getAlgorithm() + ", " +
                        "but got " + second.getChecksum().getAlgorithm() + "."
                );
                setCompareError(first, exception);
                setCompareError(second, exception);
            } else if (!Objects.equals(first.getChecksum().getHash(), second.getChecksum().getHash())) {
                CompareException exception = new MismatchException(
                    "Expected hash " + first.getChecksum().getHash() + ", " +
                        "but got " + second.getChecksum().getHash() + "."
                );
                setCompareError(first, exception);
                setCompareError(second, exception);
            }
        } else if (first.getChecksum() != null || second.getChecksum() != null) {
            if (first.getChecksum() == null) {
                setCompareError(first, new MismatchException("Checksum not computed."));
            }

            if (second.getChecksum() == null) {
                setCompareError(second, new MismatchException("Checksum not computed."));
            }
        }
    }
}
