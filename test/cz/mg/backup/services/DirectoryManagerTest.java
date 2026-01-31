package cz.mg.backup.services;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.classes.Test;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.backup.Configuration;
import cz.mg.backup.components.Progress;
import cz.mg.backup.entities.*;
import cz.mg.backup.exceptions.CompareException;
import cz.mg.backup.exceptions.MismatchException;
import cz.mg.backup.test.TestFactory;
import cz.mg.backup.test.TestProgress;
import cz.mg.test.Assert;
import cz.mg.test.Assertions;

import java.nio.file.Path;
import java.util.Calendar;
import java.util.Date;

public @Test class DirectoryManagerTest {
    private static final @Mandatory Path PATH = Configuration.getRoot(DirectoryReaderTest.class).resolve("one");

    public static void main(String[] args) {
        System.out.print("Running " + DirectoryManagerTest.class.getSimpleName() + " ... ");

        DirectoryManagerTest test = new DirectoryManagerTest();
        test.testLoadNull();
        test.testLoad();
        test.testReloadNotModified();
        test.testReloadModified();
        test.testCompare();
        test.testCompareNullFirst();
        test.testCompareNullSecond();
        test.testCompareNullBoth();
        test.testCompareSame();

        System.out.println("OK");
    }

    private final @Service DirectoryManager directoryManager = DirectoryManager.getInstance();
    private final @Service TestFactory f = TestFactory.getInstance();

    private void testLoadNull() {
        TestProgress progress = new TestProgress();
        Directory directory = directoryManager.load(null, progress);

        Assert.assertNull(directory);
        progress.verifySkip();
    }

    private void testLoad() {
        TestProgress progress = new TestProgress();
        Directory directory = directoryManager.load(PATH, progress);

        Assert.assertNotNull(directory);
        Assert.assertEquals("one", directory.getPath().getFileName().toString());
        Assert.assertEquals(0, directory.getDirectories().count());
        Assert.assertEquals(1, directory.getFiles().count());
        Assert.assertEquals("file", directory.getFiles().get(0).getPath().getFileName().toString());
        progress.verify();
    }

    private void testReloadNotModified() {
        Directory directory = directoryManager.load(PATH, new Progress());
        Assert.assertNotNull(directory);
        directory.getFiles().get(0).setChecksum(new Checksum(Algorithm.SHA256, "112233"));

        TestProgress progress = new TestProgress();
        directoryManager.reload(directory, progress);

        Assert.assertEquals("one", directory.getPath().getFileName().toString());
        Assert.assertEquals(0, directory.getDirectories().count());
        Assert.assertEquals(1, directory.getFiles().count());
        Assert.assertEquals("file", directory.getFiles().get(0).getPath().getFileName().toString());
        Assert.assertNotNull(directory.getFiles().get(0).getChecksum());
        Assert.assertEquals("112233", directory.getFiles().get(0).getChecksum().getHash());
        Assert.assertEquals(Algorithm.SHA256, directory.getFiles().get(0).getChecksum().getAlgorithm());
        progress.verify();
    }

    private void testReloadModified() {
        Directory directory = directoryManager.load(PATH, new Progress());
        Assert.assertNotNull(directory);
        directory.getFiles().get(0).setChecksum(new Checksum(Algorithm.SHA256, "112233"));
        directory.getFiles().get(0).getProperties().setModified(new Date(2000, Calendar.DECEMBER, 31));

        TestProgress progress = new TestProgress();
        directoryManager.reload(directory, progress);

        Assert.assertEquals("one", directory.getPath().getFileName().toString());
        Assert.assertEquals(0, directory.getDirectories().count());
        Assert.assertEquals(1, directory.getFiles().count());
        Assert.assertEquals("file", directory.getFiles().get(0).getPath().getFileName().toString());
        Assert.assertNull(directory.getFiles().get(0).getChecksum());
        progress.verify();
    }

    private void testCompare() {
        Directory a = f.directory("foo");
        Directory b = f.directory("bar");

        TestProgress progress = new TestProgress();
        directoryManager.compare(a, b, progress);

        Assert.assertNotNull(a.getError());
        Assert.assertNotNull(b.getError());
        Assert.assertEquals(MismatchException.class, a.getError().getClass());
        Assert.assertEquals(MismatchException.class, b.getError().getClass());
        progress.verify();
    }

    private void testCompareNullFirst() {
        Directory a = f.directory("foo");
        a.setError(new CompareException("test"));

        TestProgress progress = new TestProgress();
        directoryManager.compare(a, null, progress);

        Assert.assertNull(a.getError());
        progress.verify();
    }

    private void testCompareNullSecond() {
        Directory b = f.directory("foo");
        b.setError(new CompareException("test"));

        TestProgress progress = new TestProgress();
        directoryManager.compare(null, b, progress);

        Assert.assertNull(b.getError());
        progress.verify();
    }

    private void testCompareNullBoth() {
        TestProgress progress = new TestProgress();

        Assertions.assertThatCode(() -> {
            directoryManager.compare(null, null, progress);
        }).doesNotThrowAnyException();

        progress.verifySkip();
    }

    private void testCompareSame() {
        Directory d = f.directory("foo");
        d.setError(new CompareException("test"));

        TestProgress progress = new TestProgress();
        directoryManager.compare(d, d, progress);

        Assert.assertNull(d.getError());
        progress.verify();
    }
}
