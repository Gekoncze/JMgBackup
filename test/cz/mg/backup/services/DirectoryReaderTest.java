package cz.mg.backup.services;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.classes.Test;
import cz.mg.backup.entities.Directory;
import cz.mg.backup.entities.File;
import cz.mg.backup.entities.Settings;
import cz.mg.test.Assert;

import java.nio.file.Path;

public @Test class DirectoryReaderTest {
    public static void main(String[] args) {
        System.out.print("Running " + DirectoryReaderTest.class.getSimpleName() + " ... ");

        DirectoryReaderTest test = new DirectoryReaderTest();
        test.testRead();

        System.out.println("OK");
    }

    private final @Service DirectoryReader reader = DirectoryReader.getInstance();

    private void testRead() {
        Directory test = reader.read(Path.of("test", "cz", "mg", "backup", "test"), new Settings());
        Assert.assertEquals(true, test.getErrors().isEmpty());
        Assert.assertEquals("test", test.getPath().getFileName().toString());
        Assert.assertEquals(2, test.getFiles().count());
        Assert.assertEquals(2, test.getDirectories().count());

        Directory one = test.getDirectories().get(0);
        Assert.assertEquals(true, one.getErrors().isEmpty());
        Assert.assertEquals("one", one.getPath().getFileName().toString());
        Assert.assertEquals(1, one.getFiles().count());

        File file = one.getFiles().get(0);
        Assert.assertEquals(true, file.getErrors().isEmpty());
        Assert.assertEquals("file", file.getPath().getFileName().toString());

        Directory two = test.getDirectories().get(1);
        Assert.assertEquals(true, two.getErrors().isEmpty());
        Assert.assertEquals("two", two.getPath().getFileName().toString());
        Assert.assertEquals(1, two.getFiles().count());

        File fileLink = two.getFiles().get(0);
        Assert.assertEquals(true, fileLink.getErrors().isEmpty());
        Assert.assertEquals("fileLink", fileLink.getPath().getFileName().toString());

        File directoryLink = test.getFiles().get(0);
        Assert.assertEquals(true, directoryLink.getErrors().isEmpty());
        Assert.assertEquals("directoryLink", directoryLink.getPath().getFileName().toString());
    }
}
