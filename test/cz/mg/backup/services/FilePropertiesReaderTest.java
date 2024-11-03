package cz.mg.backup.services;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.classes.Test;
import cz.mg.backup.Configuration;
import cz.mg.backup.entities.FileProperties;
import cz.mg.test.Assert;

public @Test class FilePropertiesReaderTest {
    public static void main(String[] args) {
        System.out.print("Running " + FilePropertiesReaderTest.class.getSimpleName() + " ... ");

        FilePropertiesReaderTest test = new FilePropertiesReaderTest();
        test.testRead();

        System.out.println("OK");
    }

    private final @Service FilePropertiesReader reader = FilePropertiesReader.getInstance();

    private void testRead() {
        FileProperties fileProperties = reader.read(Configuration.FLYING_AKI_PATH);
        Assert.assertEquals(218128, fileProperties.getSize());
    }
}
