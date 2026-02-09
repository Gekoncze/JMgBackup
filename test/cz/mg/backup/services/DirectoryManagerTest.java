package cz.mg.backup.services;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.classes.Test;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.backup.Configuration;
import cz.mg.backup.components.Progress;
import cz.mg.backup.entities.*;
import cz.mg.backup.test.TestFactory;
import cz.mg.backup.test.TestProgress;
import cz.mg.test.Assert;

import java.nio.file.Path;

public @Test class DirectoryManagerTest {
    private static final @Mandatory Path PATH = Configuration.getRoot(DirectoryReaderTest.class).resolve("one");

    public static void main(String[] args) {
        System.out.print("Running " + DirectoryManagerTest.class.getSimpleName() + " ... ");

        DirectoryManagerTest test = new DirectoryManagerTest();
        test.testLoadNull();
        test.testLoad();
        test.testReloadNotModified();
        test.testReloadModified();

        System.out.println("OK");
    }

    private final @Service DirectoryManager directoryManager = DirectoryManager.getInstance();
    private final @Service TestFactory f = TestFactory.getInstance();

    private void testLoadNull() {
        TestProgress progress = new TestProgress();
        Directory directory = directoryManager.load(null, progress);

        Assert.assertNull(directory);
        progress.verifySkip();
    }

    private void testLoad() {
        TestProgress progress = new TestProgress();
        Directory directory = directoryManager.load(PATH, progress);

        Assert.assertNotNull(directory);
        Assert.assertEquals("one", directory.getPath().getFileName().toString());
        Assert.assertEquals(0, directory.getDirectories().count());
        Assert.assertEquals(1, directory.getFiles().count());
        Assert.assertEquals("file", directory.getFiles().get(0).getPath().getFileName().toString());
        progress.verify();
    }

    private void testReloadNotModified() {
        Directory directory = directoryManager.load(PATH, new Progress());
        Assert.assertNotNull(directory);
        directory.getFiles().get(0).setChecksum(new Checksum(Algorithm.SHA256, "112233"));

        TestProgress progress = new TestProgress();
        directoryManager.reload(directory, progress);

        Assert.assertEquals("one", directory.getPath().getFileName().toString());
        Assert.assertEquals(0, directory.getDirectories().count());
        Assert.assertEquals(1, directory.getFiles().count());
        Assert.assertEquals("file", directory.getFiles().get(0).getPath().getFileName().toString());

        Checksum checksum = directory.getFiles().get(0).getChecksum();
        Assert.assertNotNull(checksum);
        Assert.assertEquals("112233", checksum.getHash());
        Assert.assertEquals(Algorithm.SHA256, checksum.getAlgorithm());
        progress.verify();
    }

    private void testReloadModified() {
        Directory directory = directoryManager.load(PATH, new Progress());
        Assert.assertNotNull(directory);
        directory.getFiles().get(0).setChecksum(new Checksum(Algorithm.SHA256, "112233"));
        directory.getFiles().get(0).getProperties().setModified(f.date(2000, 12, 31));

        TestProgress progress = new TestProgress();
        directoryManager.reload(directory, progress);

        Assert.assertEquals("one", directory.getPath().getFileName().toString());
        Assert.assertEquals(0, directory.getDirectories().count());
        Assert.assertEquals(1, directory.getFiles().count());
        Assert.assertEquals("file", directory.getFiles().get(0).getPath().getFileName().toString());
        Assert.assertNull(directory.getFiles().get(0).getChecksum());
        progress.verify();
    }
}
