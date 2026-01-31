package cz.mg.backup.services;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.classes.Test;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.annotations.requirement.Optional;
import cz.mg.backup.entities.Algorithm;
import cz.mg.backup.entities.File;
import cz.mg.backup.exceptions.CompareException;
import cz.mg.backup.exceptions.MismatchException;
import cz.mg.backup.services.comparator.FileComparator;
import cz.mg.backup.test.TestFactory;
import cz.mg.test.Assert;

public @Test class FileComparatorTest {
    public static void main(String[] args) {
        System.out.print("Running " + FileComparatorTest.class.getSimpleName() + " ... ");

        FileComparatorTest test = new FileComparatorTest();
        test.testCompare();
        test.testCompareClearsCompareErrors();

        System.out.println("OK");
    }

    private final @Service FileComparator service = FileComparator.getInstance();
    private final @Service TestFactory f = TestFactory.getInstance();

    private void testCompare() {
        testCompare(
            f.file("1", f.properties(1L), f.checksum(Algorithm.SHA256, "gg")),
            f.file("2", f.properties(1L), f.checksum(Algorithm.SHA256, "gg")),
            null
        );

        testCompare(
            f.file("3", f.properties(1L), null),
            f.file("4", f.properties(1L), null),
            null
        );

        testCompare(
            f.file("5", f.properties(1L), f.checksum(Algorithm.SHA256, "gg")),
            f.file("6", f.properties(3L), f.checksum(Algorithm.SHA256, "gg")),
            MismatchException.class
        );

        testCompare(
            f.file("7", f.properties(1L), f.checksum(Algorithm.SHA256, "abc")),
            f.file("8", f.properties(1L), f.checksum(Algorithm.SHA256, "gg")),
            MismatchException.class
        );

        testCompare(
            f.file("9", f.properties(2L), f.checksum(Algorithm.SHA256, "abc")),
            f.file("10", f.properties(78L), f.checksum(Algorithm.SHA256, "")),
            MismatchException.class
        );

        testCompare(
            f.file("11", f.properties(2L), null),
            f.file("12", f.properties(78L), null),
            MismatchException.class
        );

        testCompare(
            f.file("13", f.properties(1L), f.checksum(Algorithm.SHA256, "gg")),
            f.file("14", f.properties(1L), f.checksum(Algorithm.MD5, "gg")),
            MismatchException.class
        );
    }

    private void testCompareClearsCompareErrors() {
        testCompare(
            f.file("foo", new CompareException("test")),
            f.file("foo", new CompareException("test")),
            null
        );

        testCompare(
            f.file("foo", null),
            f.file("foo", new CompareException("test")),
            null
        );

        testCompare(
            f.file("foo", new CompareException("test")),
            f.file("foo", null),
            null
        );

        testCompare(
            f.file("foo", null),
            f.file("foo", null),
            null
        );

        testCompare(
            f.file("foo", new RuntimeException("test")),
            f.file("foo", new RuntimeException("test")),
            RuntimeException.class
        );

        testCompare(
            f.file("foo", new RuntimeException("test")),
            f.file("foo", new CompareException("test")),
            RuntimeException.class,
            null
        );

        testCompare(
            f.file("foo", new CompareException("test")),
            f.file("foo", new RuntimeException("test")),
            null,
            RuntimeException.class
        );
    }

    private void testCompare(
        @Mandatory File first,
        @Mandatory File second,
        @Optional Class<? extends Exception> expectedError
    ) {
        service.compare(first, second);
        Assert.assertEquals(expectedError, getErrorClass(first));
        Assert.assertEquals(expectedError, getErrorClass(second));
    }

    private void testCompare(
        @Mandatory File first,
        @Mandatory File second,
        @Optional Class<? extends Exception> firstExpectedError,
        @Optional Class<? extends Exception> secondExpectedError
    ) {
        service.compare(first, second);
        Assert.assertEquals(firstExpectedError, getErrorClass(first));
        Assert.assertEquals(secondExpectedError, getErrorClass(second));
    }

    private @Optional Class<? extends Exception> getErrorClass(@Mandatory File file) {
        return file.getError() == null ? null : file.getError().getClass();
    }
}
