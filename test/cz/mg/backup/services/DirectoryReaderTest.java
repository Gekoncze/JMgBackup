package cz.mg.backup.services;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.classes.Test;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.backup.Configuration;
import cz.mg.backup.components.Progress;
import cz.mg.backup.entities.Directory;
import cz.mg.backup.entities.File;
import cz.mg.backup.exceptions.FileSystemException;
import cz.mg.test.Assert;
import cz.mg.test.Assertions;

import java.nio.file.Path;

public @Test class DirectoryReaderTest {
    private static final @Mandatory Path PATH = Configuration.getRoot(DirectoryReaderTest.class);

    public static void main(String[] args) {
        System.out.print("Running " + DirectoryReaderTest.class.getSimpleName() + " ... ");

        DirectoryReaderTest test = new DirectoryReaderTest();
        test.testReadUsingPath();
        test.testReadUsingDirectory();
        test.testReadSymbolicLink();

        System.out.println("OK");
    }

    private final @Service DirectoryReader reader = DirectoryReader.getInstance();

    private void testReadUsingPath() {
        Progress progress = new Progress("Test");
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

        Assert.assertEquals(0L, progress.getLimit()); // indefinite
        Assert.assertEquals(6L, progress.getValue());
    }

    private void testReadUsingDirectory() {
        Directory directory = new Directory();
        directory.setPath(PATH);
        directory.getFiles().addLast(new File());
        directory.getDirectories().addLast(new Directory());
        directory.getErrors().addLast(new FileSystemException("test"));

        Progress progress = new Progress("Test");
        reader.read(directory, progress);

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

        Assert.assertEquals(0L, progress.getLimit()); // indefinite
        Assert.assertEquals(6L, progress.getValue());
    }

    private void testReadSymbolicLink() {
        // symbolic link for given path only is followed for convenience
        Progress progress = new Progress("Test");
        Directory directory = reader.read(PATH.resolve("directoryLink"), progress);

        Assert.assertEquals("directoryLink", directory.getPath().getFileName().toString());
        Assert.assertEquals(1, directory.getFiles().count());
        Assert.assertEquals(0L, progress.getLimit()); // indefinite
        Assert.assertEquals(1L, progress.getValue());
    }
}
