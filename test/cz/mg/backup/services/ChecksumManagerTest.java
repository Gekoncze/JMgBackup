package cz.mg.backup.services;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.classes.Test;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.backup.entities.*;
import cz.mg.backup.resources.common.Common;
import cz.mg.backup.test.TestFactory;
import cz.mg.backup.test.TestProgress;
import cz.mg.collections.list.List;
import cz.mg.collections.map.Map;
import cz.mg.collections.pair.Pair;
import cz.mg.test.Assert;
import cz.mg.test.Assertions;

import java.nio.file.Path;
import java.time.Instant;

public @Test class ChecksumManagerTest {
    public static void main(String[] args) {
        System.out.print("Running " + ChecksumManagerTest.class.getSimpleName() + " ... ");

        ChecksumManagerTest test = new ChecksumManagerTest();
        test.testComputeMissingChecksum();
        test.testComputeExistingChecksum();
        test.testComputeExistingChecksumWithDifferentAlgorithm();
        test.testComputeDirectory();
        test.testClearFile();
        test.testClearDirectory();
        test.testClearMultiple();
        test.testCollectEmpty();
        test.testCollect();
        test.testRestoreEmpty();
        test.testRestore();

        System.out.println("OK");
    }

    private final @Service ChecksumManager checksumManager = ChecksumManager.getInstance();
    private final @Service DirectoryReader directoryReader = DirectoryReader.getInstance();
    private final @Service FileReader fileReader = FileReader.getInstance();
    private final @Service TestFactory f = TestFactory.getInstance();

    private void testComputeMissingChecksum() {
        File file = fileReader.read(Common.FLYING_AKI_PATH);

        TestProgress progress = new TestProgress();
        checksumManager.compute(new List<>(file), Algorithm.SHA256, progress);

        Assert.assertNotNull(file.getChecksum());
        Assert.assertEquals(Common.FLYING_AKI_HASH, file.getChecksum().getHash());
        progress.verify(1L, 1L);
    }

    private void testComputeExistingChecksum() {
        File file = fileReader.read(Common.FLYING_AKI_PATH);
        file.setChecksum(new Checksum(Algorithm.SHA256, "FF"));

        TestProgress progress = new TestProgress();
        checksumManager.compute(new List<>(file), Algorithm.SHA256, progress);

        Assert.assertNotNull(file.getChecksum());
        Assert.assertEquals(Algorithm.SHA256, file.getChecksum().getAlgorithm());
        Assert.assertEquals("FF", file.getChecksum().getHash());
        progress.verify(1L, 1L);
    }

    private void testComputeExistingChecksumWithDifferentAlgorithm() {
        File file = fileReader.read(Common.FLYING_AKI_PATH);
        file.setChecksum(new Checksum(Algorithm.MD5, "FF"));

        TestProgress progress = new TestProgress();
        checksumManager.compute(new List<>(file), Algorithm.SHA256, progress);

        Assert.assertNotNull(file.getChecksum());
        Assert.assertEquals(Algorithm.SHA256, file.getChecksum().getAlgorithm());
        Assert.assertEquals(Common.FLYING_AKI_HASH, file.getChecksum().getHash());
        progress.verify(1L, 1L);
    }

    private void testComputeDirectory() {
        File file = fileReader.read(Common.FLYING_AKI_PATH);
        file.setChecksum(new Checksum(Algorithm.MD5, "FF"));

        Directory directory = new Directory();
        directory.getFiles().addLast(file);
        directory.getProperties().setTotalFileCount(1L);
        directory.getProperties().setTotalFileCount(1L);

        TestProgress progress = new TestProgress();
        checksumManager.compute(new List<>(directory), Algorithm.SHA256, progress);

        Assert.assertNotNull(file.getChecksum());
        Assert.assertEquals(Algorithm.SHA256, file.getChecksum().getAlgorithm());
        Assert.assertEquals(Common.FLYING_AKI_HASH, file.getChecksum().getHash());
        progress.verify(1L, 1L);
    }

    private void testClearFile() {
        File file = fileReader.read(Common.FLYING_AKI_PATH);
        file.setChecksum(new Checksum(Algorithm.SHA256, "FF"));

        TestProgress progress = new TestProgress();
        checksumManager.clear(new List<>(file), progress);

        Assert.assertNull(file.getChecksum());
        progress.verify(1L, 1L);
    }

    private void testClearDirectory() {
        File fileOne = f.file("one");
        fileOne.setChecksum(new Checksum(Algorithm.SHA256, "FF"));

        File fileTwo = f.file("two");
        fileTwo.setChecksum(new Checksum(Algorithm.MD5, "AA"));

        Directory directory = f.directory("dir", fileOne, fileTwo);

        TestProgress progress = new TestProgress();
        checksumManager.clear(new List<>(directory), progress);

        Assert.assertNull(fileOne.getChecksum());
        Assert.assertNull(fileTwo.getChecksum());
        progress.verify(2L, 2L);
    }

    private void testClearMultiple() {
        File fileOne = f.file("one");
        fileOne.setChecksum(new Checksum(Algorithm.SHA256, "FF"));

        File fileTwo = f.file("two");
        fileTwo.setChecksum(new Checksum(Algorithm.MD5, "AA"));

        TestProgress progress = new TestProgress();
        checksumManager.clear(new List<>(fileOne, fileTwo), progress);

        Assert.assertNull(fileOne.getChecksum());
        Assert.assertNull(fileTwo.getChecksum());
        progress.verify(2L, 2L);
    }

    private void testCollectEmpty() {
        TestProgress progress = new TestProgress();
        var map = checksumManager.collect(null, progress);

        Assert.assertEquals(true, map.isEmpty());
        progress.verify(0L, 0L);
    }

    private void testCollect() {
        Checksum checksum = new Checksum();

        File firstFile = f.file("foo");
        firstFile.setChecksum(checksum);
        firstFile.getProperties().setCreated(createDate(1));
        firstFile.getProperties().setModified(createDate(2));

        File secondFile = f.file("bar");
        secondFile.setChecksum(null);
        secondFile.getProperties().setCreated(createDate(3));
        secondFile.getProperties().setModified(createDate(4));

        Directory directory = f.directory("root", firstFile, secondFile);

        TestProgress progress = new TestProgress();
        var map = checksumManager.collect(directory, progress);

        Assert.assertEquals(2, map.count());
        Assert.assertEquals(checksum, map.get(Path.of("root", "foo")).getKey());
        Assert.assertEquals(createDate(2), map.get(Path.of("root", "foo")).getValue());
        Assert.assertEquals(null, map.get(Path.of("root", "bar")).getKey());
        Assert.assertEquals(createDate(4), map.get(Path.of("root", "bar")).getValue());
        progress.verify(2L, 2L);
    }

    private void testRestoreEmpty() {
        TestProgress progress = new TestProgress();

        Assertions.assertThatCode(() -> {
            checksumManager.restore(null, new Map<>(), progress);
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
        File firstFile = f.file("1");
        firstFile.setChecksum(checksum1);
        firstFile.getProperties().setCreated(createDate(1));
        firstFile.getProperties().setModified(createDate(2));

        // test null -> sum 2
        File secondFile = f.file("2");
        secondFile.setChecksum(null);
        secondFile.getProperties().setCreated(createDate(3));
        secondFile.getProperties().setModified(createDate(4));

        // test sum 3a -> sum 3b
        File thirdFile = f.file("3");
        thirdFile.setChecksum(checksum3a);
        thirdFile.getProperties().setCreated(createDate(5));
        thirdFile.getProperties().setModified(createDate(6));

        // test null -> null (modified, restore skipped)
        File fourthFile = f.file("4");
        fourthFile.setChecksum(null);
        fourthFile.getProperties().setCreated(createDate(7));
        fourthFile.getProperties().setModified(createDate(8));

        // test null -> null (not in map)
        File fifthFile = f.file("5");
        fifthFile.setChecksum(null);
        fifthFile.getProperties().setCreated(createDate(9));
        fifthFile.getProperties().setModified(createDate(10));

        // test sum 6 -> sum 6 (not in map)
        File sixthFile = f.file("6");
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

        Map<Path, Pair<Checksum, Instant>> map = new Map<>();
        map.set(Path.of("1"), new Pair<>(null, createDate(2)));
        map.set(Path.of("2"), new Pair<>(checksum2, createDate(4)));
        map.set(Path.of("3"), new Pair<>(checksum3b, createDate(6)));
        map.set(Path.of("4"), new Pair<>(checksum4, createDate(30)));

        TestProgress progress = new TestProgress();
        checksumManager.restore(directory, map, progress);

        Assert.assertNull(firstFile.getChecksum());
        Assert.assertSame(checksum2, secondFile.getChecksum());
        Assert.assertSame(checksum3b, thirdFile.getChecksum());
        Assert.assertNull(fourthFile.getChecksum());
        Assert.assertNull(fifthFile.getChecksum());
        Assert.assertSame(checksum6, sixthFile.getChecksum());
        progress.verify(6L, 6L);
    }

    private @Mandatory Instant createDate(int day) {
        return f.date(2000, 1, day);
    }
}
