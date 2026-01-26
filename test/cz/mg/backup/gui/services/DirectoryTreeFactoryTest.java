package cz.mg.backup.gui.services;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.classes.Test;
import cz.mg.backup.entities.Directory;
import cz.mg.backup.entities.File;
import cz.mg.backup.gui.views.directory.DirectoryTreeEntry;
import cz.mg.backup.test.TestProgress;
import cz.mg.test.Assert;

import java.nio.file.Path;

public @Test class DirectoryTreeFactoryTest {
    public static void main(String[] args) {
        System.out.print("Running " + DirectoryTreeFactoryTest.class.getSimpleName() + " ... ");

        DirectoryTreeFactoryTest test = new DirectoryTreeFactoryTest();
        test.testNull();
        test.testCreate();

        System.out.println("OK");
    }

    private final @Service DirectoryTreeFactory factory = DirectoryTreeFactory.getInstance();

    private void testNull() {
        TestProgress progress = new TestProgress();
        Assert.assertNull(factory.create(null, progress));
        progress.verifySkip();
    }

    private void testCreate() {
        Directory root = new Directory();
        root.setPath(Path.of("parent", "root"));

        Directory childDirectory = new Directory();
        childDirectory.setPath(Path.of("parent", "root", "child directory"));

        Directory emptyDirectory = new Directory();
        emptyDirectory.setPath(Path.of("parent", "root", "empty directory"));

        File firstFile = new File();
        firstFile.setPath(Path.of("parent", "root", "child directory", "first file"));

        File secondFile = new File();
        secondFile.setPath(Path.of("parent", "root", "second file"));

        root.getDirectories().addLast(childDirectory);
        root.getDirectories().addLast(emptyDirectory);
        root.getFiles().addLast(secondFile);
        childDirectory.getFiles().addLast(firstFile);
        root.getProperties().setTotalCount(4L);

        TestProgress progress = new TestProgress();
        DirectoryTreeEntry rootEntry = factory.create(root, progress);

        Assert.assertEquals(root, rootEntry.get());
        Assert.assertEquals(0, rootEntry.getIndex());
        Assert.assertEquals(false, rootEntry.isLeaf());
        Assert.assertEquals("root", rootEntry.toString());
        Assert.assertNotNull(rootEntry.getChildren());
        Assert.assertEquals(3, rootEntry.getChildren().count());

        DirectoryTreeEntry childDirectoryEntry = rootEntry.getChildren().get(0);
        Assert.assertEquals(0, childDirectoryEntry.getIndex());
        Assert.assertEquals(false, childDirectoryEntry.isLeaf());
        Assert.assertEquals("child directory", childDirectoryEntry.toString());
        Assert.assertNotNull(childDirectoryEntry.getChildren());
        Assert.assertEquals(1, childDirectoryEntry.getChildren().count());

        DirectoryTreeEntry firstFileEntry = childDirectoryEntry.getChildren().get(0);
        Assert.assertEquals(0, firstFileEntry.getIndex());
        Assert.assertEquals(true, firstFileEntry.isLeaf());
        Assert.assertEquals("first file", firstFileEntry.toString());
        Assert.assertNotNull(firstFileEntry.getChildren());
        Assert.assertEquals(0, firstFileEntry.getChildren().count());

        DirectoryTreeEntry emptyDirectoryEntry = rootEntry.getChildren().get(1);
        Assert.assertEquals(1, emptyDirectoryEntry.getIndex());
        Assert.assertEquals(false, emptyDirectoryEntry.isLeaf());
        Assert.assertEquals("empty directory", emptyDirectoryEntry.toString());
        Assert.assertNotNull(emptyDirectoryEntry.getChildren());
        Assert.assertEquals(0, emptyDirectoryEntry.getChildren().count());

        DirectoryTreeEntry secondFileEntry = rootEntry.getChildren().get(2);
        Assert.assertEquals(2, secondFileEntry.getIndex());
        Assert.assertEquals(true, secondFileEntry.isLeaf());
        Assert.assertEquals("second file", secondFileEntry.toString());
        Assert.assertNotNull(secondFileEntry.getChildren());
        Assert.assertEquals(0, secondFileEntry.getChildren().count());

        progress.verify(5L, 5L);
    }
}
