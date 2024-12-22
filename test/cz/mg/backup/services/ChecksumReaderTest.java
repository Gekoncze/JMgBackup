package cz.mg.backup.services;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.classes.Test;
import cz.mg.backup.Configuration;
import cz.mg.backup.components.Progress;
import cz.mg.backup.entities.Algorithm;
import cz.mg.backup.entities.Checksum;
import cz.mg.test.Assert;

public @Test class ChecksumReaderTest {
    public static void main(String[] args) {
        System.out.print("Running " + ChecksumReaderTest.class.getSimpleName() + " ... ");

        ChecksumReaderTest test = new ChecksumReaderTest();
        test.testRead();

        System.out.println("OK");
    }

    private final @Service ChecksumReader reader = ChecksumReader.getInstance();

    private void testRead() {
        Progress progress = new Progress("Test");
        Checksum checksum = reader.read(Configuration.FLYING_AKI_PATH, Algorithm.SHA256, progress);

        Assert.assertEquals(Algorithm.SHA256, checksum.getAlgorithm());
        Assert.assertEquals(Configuration.FLYING_AKI_HASH, checksum.getHash());
        Assert.assertEquals(1L, progress.getLimit());
        Assert.assertEquals(1L, progress.getValue());
    }
}
