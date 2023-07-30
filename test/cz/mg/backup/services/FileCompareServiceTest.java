package cz.mg.backup.services;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.classes.Test;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.annotations.requirement.Optional;
import cz.mg.backup.entities.File;
import cz.mg.test.Assert;

public @Test class FileCompareServiceTest {
    public static void main(String[] args) {
        System.out.print("Running " + FileCompareServiceTest.class.getSimpleName() + " ... ");

        FileCompareServiceTest test = new FileCompareServiceTest();
        test.testCompare();
        test.testCompareClearsPreviousErrors();
        test.testCompareDoesNotClearOtherErrors();

        System.out.println("OK");
    }

    private final @Service FileCompareService service = FileCompareService.getInstance();

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

    private void testCompareClearsPreviousErrors() {
        File first = createFile(7L, "77");
        File second = createFile(88L, "8");
        Assert.assertEquals(0, first.getErrors().count());
        Assert.assertEquals(0, second.getErrors().count());
        service.compare(first, second);
        Assert.assertEquals(2, first.getErrors().count());
        Assert.assertEquals(2, second.getErrors().count());
        second.setSize(first.getSize());
        service.compare(first, second);
        Assert.assertEquals(1, first.getErrors().count());
        Assert.assertEquals(1, second.getErrors().count());
        second.setHash(first.getHash());
        service.compare(first, second);
        Assert.assertEquals(0, first.getErrors().count());
        Assert.assertEquals(0, second.getErrors().count());
    }

    private void testCompareDoesNotClearOtherErrors() {
        File first = createFile(7L, "77");
        File second = createFile(7L, "8");
        first.getErrors().addLast(new RuntimeException());
        Assert.assertEquals(1, first.getErrors().count());
        Assert.assertEquals(0, second.getErrors().count());
        service.compare(first, second);
        Assert.assertEquals(2, first.getErrors().count());
        Assert.assertEquals(1, second.getErrors().count());
        service.compare(first, second);
        Assert.assertEquals(2, first.getErrors().count());
        Assert.assertEquals(1, second.getErrors().count());
    }

    private void testCompare(@Mandatory File first, @Mandatory File second, boolean fail) {
        service.compare(first, second);
        Assert.assertEquals(fail, !first.getErrors().isEmpty());
        Assert.assertEquals(fail, !second.getErrors().isEmpty());
    }

    private @Mandatory File createFile(@Optional Long size, @Optional String hash) {
        File file = new File();
        file.setSize(size);
        file.setHash(hash);
        return file;
    }
}
