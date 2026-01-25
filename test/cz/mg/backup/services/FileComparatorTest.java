package cz.mg.backup.services;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.classes.Test;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.backup.entities.Algorithm;
import cz.mg.backup.entities.File;
import cz.mg.backup.exceptions.CompareException;
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
            false, false
        );

        testCompare(
            f.file("3", f.properties(1L), null),
            f.file("4", f.properties(1L), null),
            false, false
        );

        testCompare(
            f.file("5", f.properties(1L), f.checksum(Algorithm.SHA256, "gg")),
            f.file("6", f.properties(3L), f.checksum(Algorithm.SHA256, "gg")),
            true, true
        );

        testCompare(
            f.file("7", f.properties(1L), f.checksum(Algorithm.SHA256, "abc")),
            f.file("8", f.properties(1L), f.checksum(Algorithm.SHA256, "gg")),
            true, true
        );

        testCompare(
            f.file("9", f.properties(2L), f.checksum(Algorithm.SHA256, "abc")),
            f.file("10", f.properties(78L), f.checksum(Algorithm.SHA256, "")),
            true, true
        );

        testCompare(
            f.file("11", f.properties(2L), null),
            f.file("12", f.properties(78L), null),
            true, true
        );

        testCompare(
            f.file("13", f.properties(1L), f.checksum(Algorithm.SHA256, "gg")),
            f.file("14", f.properties(1L), f.checksum(Algorithm.MD5, "gg")),
            true, true
        );
    }

    private void testCompareClearsCompareErrors() {
        File first = new File();
        File second = new File();
        Assert.assertEquals(0, first.getErrors().count());
        Assert.assertEquals(0, second.getErrors().count());
        first.getErrors().addLast(new RuntimeException());
        second.getErrors().addLast(new IllegalArgumentException());
        first.getErrors().addLast(new CompareException("test"));
        second.getErrors().addLast(new CompareException("test"));
        Assert.assertEquals(2, first.getErrors().count());
        Assert.assertEquals(2, second.getErrors().count());
        service.compare(first, second);
        Assert.assertEquals(1, first.getErrors().count());
        Assert.assertEquals(1, second.getErrors().count());
        Assert.assertEquals(RuntimeException.class, first.getErrors().getFirst().getClass());
        Assert.assertEquals(IllegalArgumentException.class, second.getErrors().getFirst().getClass());
    }

    private void testCompare(
        @Mandatory File first,
        @Mandatory File second,
        boolean firstHasErrors,
        boolean secondHasErrors
    ) {
        service.compare(first, second);
        Assert.assertEquals(firstHasErrors, !first.getErrors().isEmpty());
        Assert.assertEquals(secondHasErrors, !second.getErrors().isEmpty());
    }
}
