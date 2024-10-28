package cz.mg.backup.services;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.classes.Test;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.backup.Configuration;
import cz.mg.backup.entities.*;
import cz.mg.test.Assert;

public @Test class ChecksumServiceTest {
    private static final String FAKESUM = "0123456789F";

    public static void main(String[] args) {
        System.out.print("Running " + ChecksumServiceTest.class.getSimpleName() + " ... ");

        ChecksumServiceTest test = new ChecksumServiceTest();
        test.testComputeFile();
        test.testComputeFileForceFalse();
        test.testComputeFileForceTrue();
        test.testComputeDirectory();
        test.testClearFile();
        test.testClearDirectory();

        System.out.println("OK");
    }

    private final @Service ChecksumService service = ChecksumService.getInstance();

    private void testComputeFile() {
        File file = new File();
        file.setPath(Configuration.FLYING_AKI_PATH);

        service.compute(file, createSettings(), false);

        Assert.assertNotNull(file.getChecksum());
        Assert.assertEquals(Configuration.FLYING_AKI_HASH, file.getChecksum().getHash());
    }

    private void testComputeFileForceFalse() {
        File file = new File();
        file.setPath(Configuration.FLYING_AKI_PATH);
        file.setChecksum(new Checksum(FAKESUM));

        service.compute(file, createSettings(), false);

        Assert.assertNotNull(file.getChecksum());
        Assert.assertEquals(FAKESUM, file.getChecksum().getHash());
    }

    private void testComputeFileForceTrue() {
        File file = new File();
        file.setPath(Configuration.FLYING_AKI_PATH);
        file.setChecksum(new Checksum(FAKESUM));

        service.compute(file, createSettings(), true);

        Assert.assertNotNull(file.getChecksum());
        Assert.assertEquals(Configuration.FLYING_AKI_HASH, file.getChecksum().getHash());
    }

    private void testComputeDirectory() {
        File file = new File();
        file.setPath(Configuration.FLYING_AKI_PATH);

        Directory directory = new Directory();
        directory.getFiles().addLast(file);

        service.compute(directory, createSettings(), false);

        Assert.assertNotNull(file.getChecksum());
        Assert.assertEquals(Configuration.FLYING_AKI_HASH, file.getChecksum().getHash());
    }

    private void testClearFile() {
        File file = new File();
        file.setPath(Configuration.FLYING_AKI_PATH);
        file.setChecksum(new Checksum(FAKESUM));

        service.clear(file);

        Assert.assertNull(file.getChecksum());
    }

    private void testClearDirectory() {
        File file = new File();
        file.setPath(Configuration.FLYING_AKI_PATH);
        file.setChecksum(new Checksum(FAKESUM));

        Directory directory = new Directory();
        directory.getFiles().addLast(file);

        service.clear(directory);

        Assert.assertNull(file.getChecksum());
    }

    private @Mandatory Settings createSettings() {
        Settings settings = new Settings();
        settings.setAlgorithm(Algorithm.SHA256);
        return settings;
    }
}
