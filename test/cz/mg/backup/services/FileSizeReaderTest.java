package cz.mg.backup.services;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.classes.Test;
import cz.mg.test.Assert;

import java.nio.file.Path;

public @Test class FileSizeReaderTest {
    public static void main(String[] args) {
        System.out.print("Running " + FileSizeReaderTest.class.getSimpleName() + " ... ");

        FileSizeReaderTest test = new FileSizeReaderTest();
        test.testRead();
        test.testReadSymbolicLink();

        System.out.println("OK");
    }

    private final @Service FileSizeReader reader = FileSizeReader.getInstance();

    private void testRead() {
        Assert.assertEquals(
            218128,
            reader.read(Path.of("test", "cz", "mg", "backup", "test", "FlyingAki.png"))
        );
    }

    private void testReadSymbolicLink() {
        Assert.assertNotEquals(
            reader.read(Path.of("test", "cz", "mg", "backup", "test", "one", "file")),
            reader.read(Path.of("test", "cz", "mg", "backup", "test", "two", "fileLink"))
        );
    }
}
