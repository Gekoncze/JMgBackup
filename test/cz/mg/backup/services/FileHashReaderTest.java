package cz.mg.backup.services;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.classes.Test;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.backup.Configuration;
import cz.mg.backup.entities.Algorithm;
import cz.mg.backup.entities.Settings;
import cz.mg.test.Assert;

public @Test class FileHashReaderTest {
    public static void main(String[] args) {
        System.out.print("Running " + FileHashReaderTest.class.getSimpleName() + " ... ");

        FileHashReaderTest test = new FileHashReaderTest();
        test.testRead();

        System.out.println("OK");
    }

    private final @Service FileHashReader reader = FileHashReader.getInstance();

    private void testRead() {
        Assert.assertEquals(
            Configuration.FLYING_AKI_HASH,
            reader.read(Configuration.FLYING_AKI_PATH, createSettings())
        );
    }

    private @Mandatory Settings createSettings() {
        Settings settings = new Settings();
        settings.setHashAlgorithm(Algorithm.SHA256.getCode());
        return settings;
    }
}
