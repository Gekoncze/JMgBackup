package cz.mg.backup.services;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.classes.Test;
import cz.mg.backup.entities.File;
import cz.mg.backup.test.common.Common;
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
        File file = reader.read(Common.FLYING_AKI_PATH);
        Assert.assertEquals(true, file.getErrors().isEmpty());
        Assert.assertNotNull(file.getProperties());
        Assert.assertEquals(218128, file.getProperties().getSize());
        Assert.assertEquals(null, file.getChecksum());
    }

    private void testReadSymbolicLink() {
        Assert.assertEquals(
            "fileLink",
            reader.read(Path.of("test", "cz", "mg", "backup", "test", "two", "fileLink"))
                .getPath()
                .getFileName()
                .toString()
        );

        Assert.assertEquals(
            "directoryLink",
            reader.read(Path.of("test", "cz", "mg", "backup", "test", "directoryLink"))
                .getPath()
                .getFileName()
                .toString()
        );
    }
}
