package cz.mg.backup.services;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.classes.Test;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.backup.Configuration;
import cz.mg.backup.components.Progress;
import cz.mg.backup.entities.*;
import cz.mg.collections.list.List;
import cz.mg.collections.map.Map;
import cz.mg.collections.pair.Pair;
import cz.mg.test.Assert;

import java.nio.file.Path;
import java.util.Calendar;
import java.util.Date;

public @Test class ChecksumServiceTest {
    public static void main(String[] args) {
        System.out.print("Running " + ChecksumServiceTest.class.getSimpleName() + " ... ");

        ChecksumServiceTest test = new ChecksumServiceTest();
        test.testComputeFileMissing();
        test.testComputeFileExisting();
        test.testComputeFileDifferentAlgorithm();
        test.testComputeDirectory();
        test.testClearFile();
        test.testClearDirectory();
        test.testCollectEmpty();
        test.testCollect();
        test.testRestoreEmpty();
        test.testRestore();

        System.out.println("OK");
    }

    private final @Service ChecksumService service = ChecksumService.getInstance();

    private void testComputeFileMissing() {
        File file = new File();
        file.setPath(Configuration.FLYING_AKI_PATH);

        service.compute(new List<>(file), Algorithm.SHA256, new Progress("Test"));

        Assert.assertNotNull(file.getChecksum());
        Assert.assertEquals(Configuration.FLYING_AKI_HASH, file.getChecksum().getHash());
    }

    private void testComputeFileExisting() {
        File file = new File();
        file.setPath(Configuration.FLYING_AKI_PATH);
        file.setChecksum(new Checksum(Algorithm.SHA256, "FF"));

        service.compute(new List<>(file), Algorithm.SHA256, new Progress("Test"));

        Assert.assertNotNull(file.getChecksum());
        Assert.assertEquals(Algorithm.SHA256, file.getChecksum().getAlgorithm());
        Assert.assertEquals("FF", file.getChecksum().getHash());
    }

    private void testComputeFileDifferentAlgorithm() {
        File file = new File();
        file.setPath(Configuration.FLYING_AKI_PATH);
        file.setChecksum(new Checksum(Algorithm.MD5, "FF"));

        service.compute(new List<>(file), Algorithm.SHA256, new Progress("Test"));

        Assert.assertNotNull(file.getChecksum());
        Assert.assertEquals(Algorithm.SHA256, file.getChecksum().getAlgorithm());
        Assert.assertEquals(Configuration.FLYING_AKI_HASH, file.getChecksum().getHash());
    }

    private void testComputeDirectory() {
        File file = new File();
        file.setPath(Configuration.FLYING_AKI_PATH);

        Directory directory = new Directory();
        directory.setProperties(new DirectoryProperties());
        directory.getFiles().addLast(file);

        service.compute(new List<>(directory), Algorithm.SHA256, new Progress("Test"));

        Assert.assertNotNull(file.getChecksum());
        Assert.assertEquals(Algorithm.SHA256, file.getChecksum().getAlgorithm());
        Assert.assertEquals(Configuration.FLYING_AKI_HASH, file.getChecksum().getHash());
    }

    private void testClearFile() {
        File file = new File();
        file.setPath(Configuration.FLYING_AKI_PATH);
        file.setChecksum(new Checksum(Algorithm.SHA256, "FF"));

        service.clear(new List<>(file), new Progress("Test"));

        Assert.assertNull(file.getChecksum());
    }

    private void testClearDirectory() {
        File file = new File();
        file.setPath(Configuration.FLYING_AKI_PATH);
        file.setChecksum(new Checksum(Algorithm.SHA256, "FF"));

        Directory directory = new Directory();
        directory.setProperties(new DirectoryProperties());
        directory.getFiles().addLast(file);

        service.clear(new List<>(directory), new Progress("Test"));

        Assert.assertNull(file.getChecksum());
    }

    private void testCollectEmpty() {
        var map = service.collect(null, new Progress("Test"));
        Assert.assertEquals(true, map.isEmpty());
    }

    private void testCollect() {
        Checksum checksum = new Checksum();

        File firstFile = new File();
        firstFile.setPath(Path.of("/foo"));
        firstFile.setChecksum(checksum);
        firstFile.setProperties(new FileProperties());
        firstFile.getProperties().setCreated(createDate(1));
        firstFile.getProperties().setModified(createDate(2));

        File secondFile = new File();
        secondFile.setPath(Path.of("/bar"));
        secondFile.setChecksum(null);
        secondFile.setProperties(new FileProperties());
        secondFile.getProperties().setCreated(createDate(3));
        secondFile.getProperties().setModified(createDate(4));

        Directory directory = new Directory();
        directory.getFiles().addLast(firstFile);
        directory.getFiles().addLast(secondFile);
        directory.setProperties(new DirectoryProperties());

        Map<Path, Pair<Checksum, Date>> map = service.collect(directory, new Progress("Test"));

        Assert.assertEquals(2, map.count());
        Assert.assertEquals(checksum, map.get(Path.of("/foo")).getKey());
        Assert.assertEquals(createDate(2), map.get(Path.of("/foo")).getValue());
        Assert.assertEquals(null, map.get(Path.of("/bar")).getKey());
        Assert.assertEquals(createDate(4), map.get(Path.of("/bar")).getValue());
    }

    private void testRestoreEmpty() {
        Assert.assertThatCode(() -> {
            service.restore(null, new Map<>(), new Progress("Test"));
        }).doesNotThrowAnyException();
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
        firstFile.setProperties(new FileProperties());
        firstFile.getProperties().setCreated(createDate(1));
        firstFile.getProperties().setModified(createDate(2));

        // test null -> sum 2
        File secondFile = new File();
        secondFile.setPath(Path.of("/2"));
        secondFile.setChecksum(null);
        secondFile.setProperties(new FileProperties());
        secondFile.getProperties().setCreated(createDate(3));
        secondFile.getProperties().setModified(createDate(4));

        // test sum 3a -> sum 3b
        File thirdFile = new File();
        thirdFile.setPath(Path.of("/3"));
        thirdFile.setChecksum(checksum3a);
        thirdFile.setProperties(new FileProperties());
        thirdFile.getProperties().setCreated(createDate(5));
        thirdFile.getProperties().setModified(createDate(6));

        // test null -> null (modified, restore skipped)
        File fourthFile = new File();
        fourthFile.setPath(Path.of("/4"));
        fourthFile.setChecksum(null);
        fourthFile.setProperties(new FileProperties());
        fourthFile.getProperties().setCreated(createDate(7));
        fourthFile.getProperties().setModified(createDate(8));

        // test null -> null (not in map)
        File fifthFile = new File();
        fifthFile.setPath(Path.of("/5"));
        fifthFile.setChecksum(null);
        fifthFile.setProperties(new FileProperties());
        fifthFile.getProperties().setCreated(createDate(9));
        fifthFile.getProperties().setModified(createDate(10));

        // test sum 6 -> sum 6 (not in map)
        File sixthFile = new File();
        sixthFile.setPath(Path.of("/6"));
        sixthFile.setChecksum(checksum6);
        sixthFile.setProperties(new FileProperties());
        sixthFile.getProperties().setCreated(createDate(11));
        sixthFile.getProperties().setModified(createDate(12));

        Directory directory = new Directory();
        directory.getFiles().addLast(firstFile);
        directory.getFiles().addLast(secondFile);
        directory.getFiles().addLast(thirdFile);
        directory.getFiles().addLast(fourthFile);
        directory.getFiles().addLast(fifthFile);
        directory.getFiles().addLast(sixthFile);
        directory.setProperties(new DirectoryProperties());

        Map<Path, Pair<Checksum, Date>> map = new Map<>();
        map.set(Path.of("/1"), new Pair<>(null, createDate(2)));
        map.set(Path.of("/2"), new Pair<>(checksum2, createDate(4)));
        map.set(Path.of("/3"), new Pair<>(checksum3b, createDate(6)));
        map.set(Path.of("/4"), new Pair<>(checksum4, createDate(100)));

        service.restore(directory, map, new Progress("Test"));

        Assert.assertSame(null, firstFile.getChecksum());
        Assert.assertSame(checksum2, secondFile.getChecksum());
        Assert.assertSame(checksum3b, thirdFile.getChecksum());
        Assert.assertSame(null, fourthFile.getChecksum());
        Assert.assertSame(null, fifthFile.getChecksum());
        Assert.assertSame(checksum6, sixthFile.getChecksum());
    }

    private @Mandatory Date createDate(int day) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(0);
        calendar.set(2000, Calendar.JANUARY, day);
        return calendar.getTime();
    }
}
