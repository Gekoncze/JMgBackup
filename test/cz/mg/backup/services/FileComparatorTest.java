package cz.mg.backup.services;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.classes.Test;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.annotations.requirement.Optional;
import cz.mg.backup.entities.Checksum;
import cz.mg.backup.entities.File;
import cz.mg.backup.entities.FileProperties;
import cz.mg.backup.exceptions.CompareException;
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

    private void testCompare() {
        testCompare(
            createFile(null, null),
            createFile(null, null),
            false
        );

        testCompare(
            createFile(1L, "gg"),
            createFile(1L, "gg"),
            false
        );

        testCompare(
            createFile(1L, null),
            createFile(1L, null),
            false
        );

        testCompare(
            createFile(null, "gg"),
            createFile(null, "gg"),
            false
        );

        testCompare(
            createFile(1L, "gg"),
            createFile(3L, "gg"),
            true
        );

        testCompare(
            createFile(1L, "abc"),
            createFile(1L, "gg"),
            true
        );

        testCompare(
            createFile(2L, "abc"),
            createFile(78L, ""),
            true
        );

        testCompare(
            createFile(2L, null),
            createFile(78L, null),
            true
        );

        testCompare(
            createFile(null, "abc"),
            createFile(null, ""),
            true
        );

        testCompare(
            createFile(null, null),
            createFile(null, ""),
            true
        );

        testCompare(
            createFile(2L, null),
            createFile(null, null),
            true
        );
    }

    private void testCompareClearsCompareErrors() {
        File first = new File();
        first.setProperties(new FileProperties());
        File second = new File();
        second.setProperties(new FileProperties());
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

    private void testCompare(@Mandatory File first, @Mandatory File second, boolean fail) {
        service.compare(first, second);
        Assert.assertEquals(fail, !first.getErrors().isEmpty());
        Assert.assertEquals(fail, !second.getErrors().isEmpty());
    }

    private @Mandatory File createFile(@Optional Long size, @Optional String hash) {
        File file = new File();
        file.setProperties(new FileProperties());
        file.getProperties().setSize(size);
        file.setChecksum(new Checksum(hash));
        return file;
    }
}
