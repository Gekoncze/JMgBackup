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
    private static final @Mandatory Path NAME = PATH.getFileName();

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

        Assert.assertNull(directory.getError());
        Assert.assertEquals(PATH, directory.getPath());
        Assert.assertEquals(NAME, directory.getRelativePath());
        Assert.assertEquals(1, directory.getFiles().count());
        Assert.assertEquals(2, directory.getDirectories().count());
        Assert.assertEquals(16L, directory.getProperties().getTotalSize());
        Assert.assertEquals(4L, directory.getProperties().getTotalCount());
        Assert.assertEquals(2L, directory.getProperties().getTotalFileCount());
        Assert.assertEquals(2L, directory.getProperties().getTotalDirectoryCount());

        File file = directory.getFiles().get(0);
        Assert.assertEquals(PATH.resolve("root.txt"), file.getPath());
        Assert.assertEquals(NAME.resolve("root.txt"), file.getRelativePath());
        Assert.assertEquals(3L, file.getProperties().getSize());

        Directory one = directory.getDirectories().get(0);
        Assert.assertNull(one.getError());
        Assert.assertEquals(PATH.resolve("one"), one.getPath());
        Assert.assertEquals(NAME.resolve("one"), one.getRelativePath());
        Assert.assertEquals(1, one.getFiles().count());
        Assert.assertEquals(13L, one.getProperties().getTotalSize());
        Assert.assertEquals(1L, one.getProperties().getTotalCount());
        Assert.assertEquals(1L, one.getProperties().getTotalFileCount());
        Assert.assertEquals(0L, one.getProperties().getTotalDirectoryCount());

        File innerFile = one.getFiles().get(0);
        Assert.assertNull(innerFile.getError());
        Assert.assertEquals(PATH.resolve("one").resolve("file"), innerFile.getPath());
        Assert.assertEquals(NAME.resolve("one").resolve("file"), innerFile.getRelativePath());
        Assert.assertEquals(13L, innerFile.getProperties().getSize());

        Directory two = directory.getDirectories().get(1);
        Assert.assertNull(two.getError());
        Assert.assertEquals(PATH.resolve("two"), two.getPath());
        Assert.assertEquals(NAME.resolve("two"), two.getRelativePath());
        Assert.assertEquals(0, two.getFiles().count());
        Assert.assertEquals(0L, two.getProperties().getTotalSize());
        Assert.assertEquals(0L, two.getProperties().getTotalCount());
        Assert.assertEquals(0L, two.getProperties().getTotalFileCount());
        Assert.assertEquals(0L, two.getProperties().getTotalDirectoryCount());

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
