package cz.mg.backup.services;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.classes.Test;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.backup.Configuration;
import cz.mg.backup.components.Progress;
import cz.mg.backup.entities.*;
import cz.mg.backup.exceptions.CompareException;
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
        test.testReloadNull();
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

    private void testReloadNull() {
        Progress progress = new Progress("test");
        Directory directory = directoryManager.reload(null, PATH, progress);

        Assert.assertEquals("one", directory.getPath().getFileName().toString());
        Assert.assertEquals(0, directory.getDirectories().count());
        Assert.assertEquals(1, directory.getFiles().count());
        Assert.assertEquals("file", directory.getFiles().get(0).getPath().getFileName().toString());

        Assert.assertEquals(5, progress.getLimit());
        Assert.assertEquals(5, progress.getValue());
    }

    private void testReloadNotModified() {
        Directory oldDirectory = directoryManager.reload(null, PATH, new Progress("test"));
        oldDirectory.getFiles().get(0).setChecksum(new Checksum(Algorithm.SHA256, "112233"));

        Progress progress = new Progress("test");
        Directory newDirectory = directoryManager.reload(oldDirectory, PATH, progress);

        Assert.assertEquals("one", newDirectory.getPath().getFileName().toString());
        Assert.assertEquals(0, newDirectory.getDirectories().count());
        Assert.assertEquals(1, newDirectory.getFiles().count());
        Assert.assertEquals("file", newDirectory.getFiles().get(0).getPath().getFileName().toString());
        Assert.assertNotNull(newDirectory.getFiles().get(0).getChecksum());
        Assert.assertEquals("112233", newDirectory.getFiles().get(0).getChecksum().getHash());
        Assert.assertEquals(Algorithm.SHA256, newDirectory.getFiles().get(0).getChecksum().getAlgorithm());

        Assert.assertEquals(5, progress.getLimit());
        Assert.assertEquals(5, progress.getValue());
    }

    private void testReloadModified() {
        Directory oldDirectory = directoryManager.reload(null, PATH, new Progress("test"));
        oldDirectory.getFiles().get(0).setChecksum(new Checksum(Algorithm.SHA256, "112233"));
        oldDirectory.getFiles().get(0).getProperties().setModified(new Date(2000, Calendar.DECEMBER, 31));

        Progress progress = new Progress("test");
        Directory newDirectory = directoryManager.reload(oldDirectory, PATH, progress);

        Assert.assertEquals("one", newDirectory.getPath().getFileName().toString());
        Assert.assertEquals(0, newDirectory.getDirectories().count());
        Assert.assertEquals(1, newDirectory.getFiles().count());
        Assert.assertEquals("file", newDirectory.getFiles().get(0).getPath().getFileName().toString());
        Assert.assertNull(newDirectory.getFiles().get(0).getChecksum());

        Assert.assertEquals(5, progress.getLimit());
        Assert.assertEquals(5, progress.getValue());
    }

    private void testCompare() {
        Directory a = new Directory();
        a.setPath(Path.of("foo", "bar"));

        Directory b = new Directory();
        b.setPath(Path.of("foo", "foo"));

        Progress progress = new Progress("test");
        directoryManager.compare(a, b, progress);

        Assert.assertNotNull(a.getProperties());
        Assert.assertNotNull(b.getProperties());

        Assert.assertEquals(1, a.getProperties().getTotalErrorCount());
        Assert.assertEquals(1, b.getProperties().getTotalErrorCount());

        Assert.assertEquals("Directory name differs.", a.getErrors().get(0).getMessage());
        Assert.assertEquals("Directory name differs.", b.getErrors().get(0).getMessage());

        Assert.assertEquals(3, progress.getLimit());
        Assert.assertEquals(3, progress.getValue());
    }

    private void testCompareNullFirst() {
        Directory a = new Directory();
        a.setPath(Path.of("foo", "bar"));
        a.getProperties().setTotalErrorCount(11);
        a.getErrors().addLast(new CompareException("test"));

        Progress progress = new Progress("test");
        directoryManager.compare(a, null, progress);

        Assert.assertNotNull(a.getProperties());
        Assert.assertEquals(0, a.getProperties().getTotalErrorCount());
        Assert.assertEquals(0, a.getErrors().count());

        Assert.assertEquals(3, progress.getLimit());
        Assert.assertEquals(3, progress.getValue());
    }

    private void testCompareNullSecond() {
        Directory b = new Directory();
        b.setPath(Path.of("foo", "bar"));
        b.getProperties().setTotalErrorCount(11);
        b.getErrors().addLast(new CompareException("test"));

        Progress progress = new Progress("test");
        directoryManager.compare(null, b, progress);

        Assert.assertNotNull(b.getProperties());
        Assert.assertEquals(0, b.getProperties().getTotalErrorCount());
        Assert.assertEquals(0, b.getErrors().count());

        Assert.assertEquals(3, progress.getLimit());
        Assert.assertEquals(3, progress.getValue());
    }

    private void testCompareNullBoth() {
        Progress progress = new Progress("test");

        Assertions.assertThatCode(() -> {
            directoryManager.compare(null, null, progress);
        }).doesNotThrowAnyException();

        Assert.assertEquals(3, progress.getLimit());
        Assert.assertEquals(3, progress.getValue());
    }

    private void testCompareSame() {
        Directory d = new Directory();
        d.setPath(Path.of("foo", "bar"));
        d.getProperties().setTotalErrorCount(11);
        d.getErrors().addLast(new CompareException("test"));

        Progress progress = new Progress("test");
        directoryManager.compare(d, d, progress);

        Assert.assertNotNull(d.getProperties());
        Assert.assertEquals(0, d.getProperties().getTotalErrorCount());
        Assert.assertEquals(0, d.getErrors().count());

        Assert.assertEquals(3, progress.getLimit());
        Assert.assertEquals(3, progress.getValue());
    }
}
