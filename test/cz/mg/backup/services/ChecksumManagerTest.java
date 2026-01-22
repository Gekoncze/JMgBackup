package cz.mg.backup.services;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.classes.Test;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.backup.components.Progress;
import cz.mg.backup.entities.*;
import cz.mg.backup.test.common.Common;
import cz.mg.collections.map.Map;
import cz.mg.collections.pair.Pair;
import cz.mg.test.Assert;
import cz.mg.test.Assertions;

import java.nio.file.Path;
import java.util.Calendar;
import java.util.Date;

public @Test class ChecksumManagerTest {
    public static void main(String[] args) {
        System.out.print("Running " + ChecksumManagerTest.class.getSimpleName() + " ... ");

        ChecksumManagerTest test = new ChecksumManagerTest();
        test.testComputeFileMissing();
        test.testComputeFileExisting();
        test.testComputeFileDifferentAlgorithm();
        test.testClearFile();
        test.testCollectEmpty();
        test.testCollect();
        test.testRestoreEmpty();
        test.testRestore();

        System.out.println("OK");
    }

    private final @Service ChecksumManager manager = ChecksumManager.getInstance();

    private void testComputeFileMissing() {
        File file = new File();
        file.setPath(Common.FLYING_AKI_PATH);

        Progress progress = new Progress("Test");
        manager.compute(file, Algorithm.SHA256, progress);

        Assert.assertNotNull(file.getChecksum());
        Assert.assertEquals(Common.FLYING_AKI_HASH, file.getChecksum().getHash());
        Assert.assertEquals(0L, progress.getLimit());
        Assert.assertEquals(0L, progress.getValue());
    }

    private void testComputeFileExisting() {
        File file = new File();
        file.setPath(Common.FLYING_AKI_PATH);
        file.setChecksum(new Checksum(Algorithm.SHA256, "FF"));

        Progress progress = new Progress("Test");
        manager.compute(file, Algorithm.SHA256, progress);

        Assert.assertNotNull(file.getChecksum());
        Assert.assertEquals(Algorithm.SHA256, file.getChecksum().getAlgorithm());
        Assert.assertEquals("FF", file.getChecksum().getHash());
        Assert.assertEquals(0L, progress.getLimit());
        Assert.assertEquals(0L, progress.getValue());
    }

    private void testComputeFileDifferentAlgorithm() {
        File file = new File();
        file.setPath(Common.FLYING_AKI_PATH);
        file.setChecksum(new Checksum(Algorithm.MD5, "FF"));

        Progress progress = new Progress("Test");
        manager.compute(file, Algorithm.SHA256, progress);

        Assert.assertNotNull(file.getChecksum());
        Assert.assertEquals(Algorithm.SHA256, file.getChecksum().getAlgorithm());
        Assert.assertEquals(Common.FLYING_AKI_HASH, file.getChecksum().getHash());
        Assert.assertEquals(0L, progress.getLimit());
        Assert.assertEquals(0L, progress.getValue());
    }

    private void testClearFile() {
        File file = new File();
        file.setPath(Common.FLYING_AKI_PATH);
        file.setChecksum(new Checksum(Algorithm.SHA256, "FF"));

        manager.clear(file);

        Assert.assertNull(file.getChecksum());
    }

    private void testCollectEmpty() {
        Progress progress = new Progress("Test");
        var map = manager.collect(null, progress);

        Assert.assertEquals(true, map.isEmpty());
        Assert.assertEquals(0L, progress.getLimit());
        Assert.assertEquals(0L, progress.getValue());
    }

    private void testCollect() {
        Checksum checksum = new Checksum();

        File firstFile = new File();
        firstFile.setPath(Path.of("/foo"));
        firstFile.setChecksum(checksum);
        firstFile.getProperties().setCreated(createDate(1));
        firstFile.getProperties().setModified(createDate(2));

        File secondFile = new File();
        secondFile.setPath(Path.of("/bar"));
        secondFile.setChecksum(null);
        secondFile.getProperties().setCreated(createDate(3));
        secondFile.getProperties().setModified(createDate(4));

        Directory directory = new Directory();
        directory.getFiles().addLast(firstFile);
        directory.getFiles().addLast(secondFile);
        directory.getProperties().setTotalFileCount(2L);

        Progress progress = new Progress("Test");
        Map<Path, Pair<Checksum, Date>> map = manager.collect(directory, progress);

        Assert.assertEquals(2, map.count());
        Assert.assertEquals(checksum, map.get(Path.of("/foo")).getKey());
        Assert.assertEquals(createDate(2), map.get(Path.of("/foo")).getValue());
        Assert.assertEquals(null, map.get(Path.of("/bar")).getKey());
        Assert.assertEquals(createDate(4), map.get(Path.of("/bar")).getValue());
        Assert.assertEquals(2L, progress.getLimit());
        Assert.assertEquals(2L, progress.getValue());
    }

    private void testRestoreEmpty() {
        Progress progress = new Progress("Test");

        Assertions.assertThatCode(() -> {
            manager.restore(null, new Map<>(), progress);
        }).doesNotThrowAnyException();

        Assert.assertEquals(0L, progress.getLimit());
        Assert.assertEquals(0L, progress.getValue());
    }

    private void testRestore() {
        Checksum checksum1 = new Checksum();
        Checksum checksum2 = new Checksum();
        Checksum checksum3a = new Checksum();
        Checksum checksum3b = new Checksum();
        Checksum checksum4 = new Checksum();
        Checksum checksum6 = new Checksum();

        // test sum 1 -> null
        File firstFile = new File();
        firstFile.setPath(Path.of("/1"));
        firstFile.setChecksum(checksum1);
        firstFile.getProperties().setCreated(createDate(1));
        firstFile.getProperties().setModified(createDate(2));

        // test null -> sum 2
        File secondFile = new File();
        secondFile.setPath(Path.of("/2"));
        secondFile.setChecksum(null);
        secondFile.getProperties().setCreated(createDate(3));
        secondFile.getProperties().setModified(createDate(4));

        // test sum 3a -> sum 3b
        File thirdFile = new File();
        thirdFile.setPath(Path.of("/3"));
        thirdFile.setChecksum(checksum3a);
        thirdFile.getProperties().setCreated(createDate(5));
        thirdFile.getProperties().setModified(createDate(6));

        // test null -> null (modified, restore skipped)
        File fourthFile = new File();
        fourthFile.setPath(Path.of("/4"));
        fourthFile.setChecksum(null);
        fourthFile.getProperties().setCreated(createDate(7));
        fourthFile.getProperties().setModified(createDate(8));

        // test null -> null (not in map)
        File fifthFile = new File();
        fifthFile.setPath(Path.of("/5"));
        fifthFile.setChecksum(null);
        fifthFile.getProperties().setCreated(createDate(9));
        fifthFile.getProperties().setModified(createDate(10));

        // test sum 6 -> sum 6 (not in map)
        File sixthFile = new File();
        sixthFile.setPath(Path.of("/6"));
        sixthFile.setChecksum(checksum6);
        sixthFile.getProperties().setCreated(createDate(11));
        sixthFile.getProperties().setModified(createDate(12));

        Directory directory = new Directory();
        directory.getFiles().addLast(firstFile);
        directory.getFiles().addLast(secondFile);
        directory.getFiles().addLast(thirdFile);
        directory.getFiles().addLast(fourthFile);
        directory.getFiles().addLast(fifthFile);
        directory.getFiles().addLast(sixthFile);
        directory.getProperties().setTotalFileCount(6L);

        Map<Path, Pair<Checksum, Date>> map = new Map<>();
        map.set(Path.of("/1"), new Pair<>(null, createDate(2)));
        map.set(Path.of("/2"), new Pair<>(checksum2, createDate(4)));
        map.set(Path.of("/3"), new Pair<>(checksum3b, createDate(6)));
        map.set(Path.of("/4"), new Pair<>(checksum4, createDate(100)));

        Progress progress = new Progress("Test");
        manager.restore(directory, map, progress);

        Assert.assertSame(null, firstFile.getChecksum());
        Assert.assertSame(checksum2, secondFile.getChecksum());
        Assert.assertSame(checksum3b, thirdFile.getChecksum());
        Assert.assertSame(null, fourthFile.getChecksum());
        Assert.assertSame(null, fifthFile.getChecksum());
        Assert.assertSame(checksum6, sixthFile.getChecksum());
        Assert.assertEquals(6L, progress.getLimit());
        Assert.assertEquals(6L, progress.getValue());
    }

    private @Mandatory Date createDate(int day) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(0);
        calendar.set(2000, Calendar.JANUARY, day);
        return calendar.getTime();
    }
}
