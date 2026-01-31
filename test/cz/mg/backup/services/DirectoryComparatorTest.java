package cz.mg.backup.services;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.classes.Test;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.backup.entities.*;
import cz.mg.backup.services.comparator.DirectoryComparator;
import cz.mg.backup.test.TestFactory;
import cz.mg.backup.test.TestProgress;
import cz.mg.test.Assert;
import cz.mg.test.exceptions.AssertException;

public @Test class DirectoryComparatorTest {
    public static void main(String[] args) {
        System.out.print("Running " + DirectoryComparatorTest.class.getSimpleName() + " ... ");

        DirectoryComparatorTest test = new DirectoryComparatorTest();
        test.testEqual();
        test.testMissingDirectory();
        test.testMissingFile();
        test.testDifferentFile();
        test.testRecursive();

        System.out.println("OK");
    }

    private final @Service DirectoryComparator service = DirectoryComparator.getInstance();
    private final @Service TestFactory f = TestFactory.getInstance();

    private void testEqual() {
        testEqual(f.directory("foo"), f.directory("foo"), 2, 2);
        testEqual(
            f.directory("foobar", f.directory("foo"), f.file("bar")),
            f.directory("foobar", f.directory("foo"), f.file("bar")),
            10L, 10L
        );
        testEqual(
            f.directory(
                "root",
                f.directory(
                    "foobar",
                    f.file("foo"),
                    f.file("bar", f.properties(1L), f.checksum("11"))
                )
            ),
            f.directory(
                "root",
                f.directory(
                    "foobar",
                    f.file("foo"),
                    f.file("bar", f.properties(1L), f.checksum("11"))
                )
            ),
            14L, 14L
        );
    }

    private void testEqual(
        @Mandatory Directory first,
        @Mandatory Directory second,
        long expectedLimit,
        long expectedValue
    ) {
        TestProgress progress = new TestProgress();
        service.compare(first, second, progress);
        assertNoErrors(first);
        assertNoErrors(second);
        progress.verify(expectedLimit, expectedValue);
    }

    private void assertNoErrors(@Mandatory Directory directory) {
        if (directory.getError() != null) {
            throw new AssertException("Unexpected error.", directory.getError());
        }

        for (Directory child : directory.getDirectories()) {
            assertNoErrors(child);
        }

        for (File child : directory.getFiles()) {
            assertNoError(child);
        }
    }

    private void assertNoError(@Mandatory File file) {
        if (file.getError() != null) {
            throw new AssertException("Unexpected error.", file.getError());
        }
    }

    private void testMissingDirectory() {
        testMissingFirstDirectory();
        testMissingSecondDirectory();
        testMissingBothDirectories();
    }

    private void testMissingFirstDirectory() {
        Directory firstL1 = f.directory("L1");
        Directory secondL2 = f.directory("L2");
        Directory secondL1 = f.directory("L1", secondL2);

        TestProgress progress = new TestProgress();
        service.compare(firstL1, secondL1, progress);

        Assert.assertNull(firstL1.getError());
        Assert.assertNotNull(secondL2.getError());
        Assert.assertNull(secondL1.getError());
        progress.verify(4L, 4L);
    }

    private void testMissingSecondDirectory() {
        Directory firstL2 = f.directory("L2");
        Directory firstL1 = f.directory("L1", firstL2);
        Directory secondL1 = f.directory("L1");

        TestProgress progress = new TestProgress();
        service.compare(firstL1, secondL1, progress);

        Assert.assertNotNull(firstL2.getError());
        Assert.assertNull(firstL1.getError());
        Assert.assertNull(secondL1.getError());
        progress.verify(4L, 4L);
    }

    private void testMissingBothDirectories() {
        Directory firstL2 = f.directory("L2 first");
        Directory firstL1 = f.directory("L1", firstL2);
        Directory secondL2 = f.directory("L2 second");
        Directory secondL1 = f.directory("L1", secondL2);

        TestProgress progress = new TestProgress();
        service.compare(firstL1, secondL1, progress);

        Assert.assertNotNull(firstL2.getError());
        Assert.assertNull(firstL1.getError());
        Assert.assertNotNull(secondL2.getError());
        Assert.assertNull(secondL1.getError());
        progress.verify(6L, 6L);
    }

    private void testMissingFile() {
        testMissingFirstFile();
        testMissingSecondFile();
        testMissingBothFiles();
    }

    private void testMissingFirstFile() {
        Directory firstL1 = f.directory("L1");
        File secondL2 = f.file("L2");
        Directory secondL1 = f.directory("L1", secondL2);

        TestProgress progress = new TestProgress();
        service.compare(firstL1, secondL1, progress);

        Assert.assertNull(firstL1.getError());
        Assert.assertNotNull(secondL2.getError());
        Assert.assertNull(secondL1.getError());
        progress.verify(4L, 4L);
    }

    private void testMissingSecondFile() {
        File firstL2 = f.file("L2");
        Directory firstL1 = f.directory("L1", firstL2);
        Directory secondL1 = f.directory("L1");

        TestProgress progress = new TestProgress();
        service.compare(firstL1, secondL1, progress);

        Assert.assertNotNull(firstL2.getError());
        Assert.assertNull(firstL1.getError());
        Assert.assertNull(secondL1.getError());
        progress.verify(4L, 4L);
    }

    private void testMissingBothFiles() {
        File firstL2 = f.file("L2 first");
        Directory firstL1 = f.directory("L1", firstL2);
        File secondL2 = f.file("L2 second");
        Directory secondL1 = f.directory("L1", secondL2);

        TestProgress progress = new TestProgress();
        service.compare(firstL1, secondL1, progress);

        Assert.assertNotNull(firstL2.getError());
        Assert.assertNull(firstL1.getError());
        Assert.assertNotNull(secondL2.getError());
        Assert.assertNull(secondL1.getError());
        progress.verify(6L, 6L);
    }

    private void testDifferentFile() {
        File firstFile = f.file("foo", f.properties(1L), null);
        File secondFile = f.file("foo", f.properties(2L), null);
        Directory firstDirectory = f.directory("root", firstFile);
        Directory secondDirectory = f.directory("root", secondFile);

        TestProgress progress = new TestProgress();
        service.compare(firstDirectory, secondDirectory, progress);

        Assert.assertNull(firstDirectory.getError());
        Assert.assertNotNull(firstFile.getError());
        Assert.assertNull(secondDirectory.getError());
        Assert.assertNotNull(secondFile.getError());
        progress.verify(6L, 6L);
    }

    private void testRecursive() {
        Directory firstL3 = f.directory("L3 first");
        Directory firstL2 = f.directory("L2", firstL3);
        Directory firstL2a = f.directory("L2a");
        Directory firstL2b = f.directory("L2b");
        Directory firstL1 = f.directory("L1", firstL2a, firstL2, firstL2b);
        Directory secondL3 = f.directory("L3 second");
        Directory secondL2 = f.directory("L2", secondL3);
        Directory secondL2a = f.directory("L2a");
        Directory secondL2b = f.directory("L2b");
        Directory secondL1 = f.directory("L1", secondL2a, secondL2, secondL2b);

        TestProgress progress = new TestProgress();
        service.compare(firstL1, secondL1, progress);

        Assert.assertNull(firstL1.getError());
        Assert.assertNull(firstL2a.getError());
        Assert.assertNull(firstL2.getError());
        Assert.assertNull(firstL2b.getError());
        Assert.assertNotNull(firstL3.getError());

        Assert.assertNull(secondL1.getError());
        Assert.assertNull(secondL2a.getError());
        Assert.assertNull(secondL2.getError());
        Assert.assertNull(secondL2b.getError());
        Assert.assertNotNull(secondL3.getError());

        progress.verify(18L, 18L);
    }
}
