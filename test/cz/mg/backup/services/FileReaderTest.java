package cz.mg.backup.services;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.classes.Test;
import cz.mg.backup.entities.File;
import cz.mg.backup.resources.common.Common;
import cz.mg.test.Assert;

import java.nio.file.Path;

public @Test class FileReaderTest {
    public static void main(String[] args) {
        System.out.print("Running " + FileReaderTest.class.getSimpleName() + " ... ");

        FileReaderTest test = new FileReaderTest();
        test.testRead();
        test.restReadError();

        System.out.println("OK");
    }

    private final @Service FileReader reader = FileReader.getInstance();

    private void testRead() {
        Path path = Common.FLYING_AKI_PATH;
        File file = reader.read(path);
        Assert.assertEquals(path, file.getPath());
        Assert.assertEquals(path.getFileName(), file.getRelativePath());
        Assert.assertNull(file.getError());
        Assert.assertNotNull(file.getProperties());
        Assert.assertEquals(218128, file.getProperties().getSize());
        Assert.assertEquals(null, file.getChecksum());
    }

    private void restReadError() {
        Path path = Common.PATH.resolve("nonexistent");
        File file = reader.read(path);
        Assert.assertEquals(path, file.getPath());
        Assert.assertEquals(path.getFileName(), file.getRelativePath());
        Assert.assertNotNull(file.getError());
        Assert.assertNotNull(file.getProperties());
        Assert.assertEquals(0, file.getProperties().getSize());
        Assert.assertEquals(null, file.getChecksum());
    }
}
