package cz.mg.backup.services;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.classes.Test;
import cz.mg.backup.Configuration;
import cz.mg.backup.components.Progress;
import cz.mg.backup.entities.*;
import cz.mg.collections.list.List;
import cz.mg.test.Assert;

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
}
