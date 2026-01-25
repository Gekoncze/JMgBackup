package cz.mg.backup.services;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.classes.Test;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.backup.entities.*;
import cz.mg.backup.exceptions.CompareException;
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
        test.testCompareClearsCompareErrors();
        test.testClearCompareErrors();

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
        if (!directory.getErrors().isEmpty()) {
            throw new AssertException("Unexpected error.", directory.getErrors().getFirst());
        }

        for (Directory child : directory.getDirectories()) {
            assertNoErrors(child);
        }

        for (File child : directory.getFiles()) {
            assertNoErrors(child);
        }
    }

    private void assertNoErrors(@Mandatory File file) {
        if (!file.getErrors().isEmpty()) {
            throw new AssertException("Unexpected error.", file.getErrors().getFirst());
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

        Assert.assertEquals(0, firstL1.getErrors().count());
        Assert.assertEquals(1, secondL2.getErrors().count());
        Assert.assertEquals(0, secondL1.getErrors().count());
        progress.verify(4L, 4L);
    }

    private void testMissingSecondDirectory() {
        Directory firstL2 = f.directory("L2");
        Directory firstL1 = f.directory("L1", firstL2);
        Directory secondL1 = f.directory("L1");

        TestProgress progress = new TestProgress();
        service.compare(firstL1, secondL1, progress);

        Assert.assertEquals(1, firstL2.getErrors().count());
        Assert.assertEquals(0, firstL1.getErrors().count());
        Assert.assertEquals(0, secondL1.getErrors().count());
        progress.verify(4L, 4L);
    }

    private void testMissingBothDirectories() {
        Directory firstL2 = f.directory("L2 first");
        Directory firstL1 = f.directory("L1", firstL2);
        Directory secondL2 = f.directory("L2 second");
        Directory secondL1 = f.directory("L1", secondL2);

        TestProgress progress = new TestProgress();
        service.compare(firstL1, secondL1, progress);

        Assert.assertEquals(1, firstL2.getErrors().count());
        Assert.assertEquals(0, firstL1.getErrors().count());
        Assert.assertEquals(1, secondL2.getErrors().count());
        Assert.assertEquals(0, secondL1.getErrors().count());
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

        Assert.assertEquals(0, firstL1.getErrors().count());
        Assert.assertEquals(1, secondL2.getErrors().count());
        Assert.assertEquals(0, secondL1.getErrors().count());
        progress.verify(4L, 4L);
    }

    private void testMissingSecondFile() {
        File firstL2 = f.file("L2");
        Directory firstL1 = f.directory("L1", firstL2);
        Directory secondL1 = f.directory("L1");

        TestProgress progress = new TestProgress();
        service.compare(firstL1, secondL1, progress);

        Assert.assertEquals(1, firstL2.getErrors().count());
        Assert.assertEquals(0, firstL1.getErrors().count());
        Assert.assertEquals(0, secondL1.getErrors().count());
        progress.verify(4L, 4L);
    }

    private void testMissingBothFiles() {
        File firstL2 = f.file("L2 first");
        Directory firstL1 = f.directory("L1", firstL2);
        File secondL2 = f.file("L2 second");
        Directory secondL1 = f.directory("L1", secondL2);

        TestProgress progress = new TestProgress();
        service.compare(firstL1, secondL1, progress);

        Assert.assertEquals(1, firstL2.getErrors().count());
        Assert.assertEquals(0, firstL1.getErrors().count());
        Assert.assertEquals(1, secondL2.getErrors().count());
        Assert.assertEquals(0, secondL1.getErrors().count());
        progress.verify(6L, 6L);
    }

    private void testDifferentFile() {
        File firstFile = f.file("foo", f.properties(1L), null);
        File secondFile = f.file("foo", f.properties(2L), null);
        Directory firstDirectory = f.directory("root", firstFile);
        Directory secondDirectory = f.directory("root", secondFile);

        TestProgress progress = new TestProgress();
        service.compare(firstDirectory, secondDirectory, progress);

        Assert.assertEquals(0, firstDirectory.getErrors().count());
        Assert.assertEquals(1, firstFile.getErrors().count());
        Assert.assertEquals(0, secondDirectory.getErrors().count());
        Assert.assertEquals(1, secondFile.getErrors().count());
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

        Assert.assertEquals(0, firstL1.getErrors().count());
        Assert.assertEquals(0, firstL2a.getErrors().count());
        Assert.assertEquals(0, firstL2.getErrors().count());
        Assert.assertEquals(0, firstL2b.getErrors().count());
        Assert.assertEquals(1, firstL3.getErrors().count());

        Assert.assertEquals(0, secondL1.getErrors().count());
        Assert.assertEquals(0, secondL2a.getErrors().count());
        Assert.assertEquals(0, secondL2.getErrors().count());
        Assert.assertEquals(0, secondL2b.getErrors().count());
        Assert.assertEquals(1, secondL3.getErrors().count());

        progress.verify(18L, 18L);
    }

    private void testCompareClearsCompareErrors() {
        Directory first = f.directory("root");
        Directory second = f.directory("root");

        Assert.assertEquals(0, first.getErrors().count());
        Assert.assertEquals(0, second.getErrors().count());

        first.getErrors().addLast(new RuntimeException());
        second.getErrors().addLast(new IllegalArgumentException());
        first.getErrors().addLast(new CompareException("test"));
        second.getErrors().addLast(new CompareException("test"));
        first.getProperties().setTotalErrorCount(2);
        second.getProperties().setTotalErrorCount(2);

        Assert.assertEquals(2, first.getErrors().count());
        Assert.assertEquals(2, second.getErrors().count());
        Assert.assertEquals(2, first.getProperties().getTotalErrorCount());
        Assert.assertEquals(2, second.getProperties().getTotalErrorCount());

        TestProgress progress = new TestProgress();
        service.compare(first, second, progress);

        Assert.assertEquals(1, first.getErrors().count());
        Assert.assertEquals(1, second.getErrors().count());
        Assert.assertEquals(RuntimeException.class, first.getErrors().getFirst().getClass());
        Assert.assertEquals(IllegalArgumentException.class, second.getErrors().getFirst().getClass());
        progress.verify(2L, 2L);
    }

    private void testClearCompareErrors() {
        Directory subdirectory = f.directory("subdirectory");
        subdirectory.getErrors().addLast(new CompareException("0"));

        Directory directory = f.directory("directory", subdirectory);
        directory.getErrors().addLast(new RuntimeException());
        directory.getErrors().addLast(new CompareException("1"));
        directory.getErrors().addLast(new CompareException("2"));
        directory.getErrors().addLast(new IllegalArgumentException());

        Assert.assertEquals(1, subdirectory.getErrors().count());
        Assert.assertEquals(4, directory.getErrors().count());

        TestProgress firstProgress = new TestProgress();
        service.compare(directory, null, firstProgress);

        Assert.assertEquals(1, subdirectory.getErrors().count());
        Assert.assertEquals(3, directory.getErrors().count());
        Assert.assertEquals(RuntimeException.class, directory.getErrors().get(0).getClass());
        Assert.assertEquals(IllegalArgumentException.class, directory.getErrors().get(1).getClass());
        firstProgress.verify(4L, 3L);

        subdirectory.getErrors().addLast(new CompareException("0"));
        directory.getErrors().addLast(new CompareException("1"));
        directory.getErrors().addLast(new CompareException("2"));

        Assert.assertEquals(2, subdirectory.getErrors().count());
        Assert.assertEquals(5, directory.getErrors().count());

        TestProgress secondProgress = new TestProgress();
        service.compare(null, directory, secondProgress);

        Assert.assertEquals(1, subdirectory.getErrors().count());
        Assert.assertEquals(3, directory.getErrors().count());
        Assert.assertEquals(RuntimeException.class, directory.getErrors().get(0).getClass());
        Assert.assertEquals(IllegalArgumentException.class, directory.getErrors().get(1).getClass());
        secondProgress.verify(4L, 3L);
    }
}
