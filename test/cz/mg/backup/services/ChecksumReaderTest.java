package cz.mg.backup.services;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.classes.Test;
import cz.mg.backup.Configuration;
import cz.mg.backup.entities.Algorithm;
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
        Assert.assertEquals(
            Configuration.FLYING_AKI_HASH,
            reader.read(Configuration.FLYING_AKI_PATH, Algorithm.SHA256).getHash()
        );
    }
}
