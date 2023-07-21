package cz.mg.backup.services;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.classes.Test;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.backup.entities.Settings;
import cz.mg.test.Assert;

import java.nio.file.Path;

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
            "357e9abbbe50922c6c0b31cb8f4371add40deaf39924e54acdbc691b7975f576",
            reader.read(Path.of("test", "cz", "mg", "backup", "test", "FlyingAki.png"), createSettings())
        );
    }

    private @Mandatory Settings createSettings() {
        Settings settings = new Settings();
        settings.setHashAlgorithm("SHA-256");
        return settings;
    }
}
