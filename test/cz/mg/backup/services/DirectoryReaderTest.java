package cz.mg.backup.services;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.classes.Test;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.backup.Configuration;
import cz.mg.backup.entities.Directory;
import cz.mg.backup.entities.File;
import cz.mg.backup.test.TestProgress;
import cz.mg.test.Assert;

import java.nio.file.Path;

public @Test class DirectoryReaderTest {
    private static final @Mandatory Path PATH = Configuration.getRoot(DirectoryReaderTest.class);

    public static void main(String[] args) {
        System.out.print("Running " + DirectoryReaderTest.class.getSimpleName() + " ... ");

        DirectoryReaderTest test = new DirectoryReaderTest();
        test.testReadDirectory();
        test.testReadDirectorySymbolicLink();

        System.out.println("OK");
    }

    private final @Service DirectoryReader reader = DirectoryReader.getInstance();

    private void testReadDirectory() {
        TestProgress progress = new TestProgress();
        Directory directory = reader.read(PATH, progress);

        Assert.assertEquals(true, directory.getErrors().isEmpty());
        Assert.assertEquals(PATH, directory.getPath());
        Assert.assertEquals(1, directory.getFiles().count());
        Assert.assertEquals(2, directory.getDirectories().count());

        File file = directory.getFiles().get(0);
        Assert.assertEquals("root.txt", file.getPath().getFileName().toString());

        Directory one = directory.getDirectories().get(0);
        Assert.assertEquals(true, one.getErrors().isEmpty());
        Assert.assertEquals("one", one.getPath().getFileName().toString());
        Assert.assertEquals(1, one.getFiles().count());

        File innerFile = one.getFiles().get(0);
        Assert.assertEquals(true, innerFile.getErrors().isEmpty());
        Assert.assertEquals("file", innerFile.getPath().getFileName().toString());

        Directory two = directory.getDirectories().get(1);
        Assert.assertEquals(true, two.getErrors().isEmpty());
        Assert.assertEquals("two", two.getPath().getFileName().toString());
        Assert.assertEquals(0, two.getFiles().count());

        progress.verify(0L, 6L);
    }

    private void testReadDirectorySymbolicLink() {
        // symbolic link for given path only is followed for convenience
        TestProgress progress = new TestProgress();
        Directory directory = reader.read(PATH.resolve("directoryLink"), progress);

        Assert.assertEquals("directoryLink", directory.getPath().getFileName().toString());
        Assert.assertEquals(1, directory.getFiles().count());
        progress.verify(0L, 1L);
    }
}
