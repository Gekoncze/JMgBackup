package cz.mg.backup.services;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.classes.Test;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.backup.entities.File;
import cz.mg.backup.entities.Settings;
import cz.mg.test.Assert;

import java.nio.file.Path;

public @Test class FileReaderTest {
    public static void main(String[] args) {
        System.out.print("Running " + FileReaderTest.class.getSimpleName() + " ... ");

        FileReaderTest test = new FileReaderTest();
        test.testRead();
        test.testReadSymbolicLink();

        System.out.println("OK");
    }

    private final @Service FileReader reader = FileReader.getInstance();

    private void testRead() {
        File file = reader.read(Path.of("test", "cz", "mg", "backup", "test", "FlyingAki.png"), createSettings());
        Assert.assertEquals(true, file.getErrors().isEmpty());
        Assert.assertNotNull(file.getProperties());
        Assert.assertEquals(218128, file.getProperties().getSize());
        Assert.assertEquals(
            "357e9abbbe50922c6c0b31cb8f4371add40deaf39924e54acdbc691b7975f576",
            file.getProperties().getHash()
        );
    }

    private void testReadSymbolicLink() {
        Assert.assertEquals(
            "fileLink",
            reader.read(Path.of("test", "cz", "mg", "backup", "test", "two", "fileLink"), new Settings())
                .getPath()
                .getFileName()
                .toString()
        );

        Assert.assertEquals(
            "directoryLink",
            reader.read(Path.of("test", "cz", "mg", "backup", "test", "directoryLink"), new Settings())
                .getPath()
                .getFileName()
                .toString()
        );
    }

    private @Mandatory Settings createSettings() {
        Settings settings = new Settings();
        settings.setHashAlgorithm("SHA-256");
        return settings;
    }
}
