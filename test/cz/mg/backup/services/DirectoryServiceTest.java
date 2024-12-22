package cz.mg.backup.services;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.classes.Test;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.backup.components.Progress;
import cz.mg.backup.entities.Directory;
import cz.mg.backup.entities.File;
import cz.mg.test.Assert;
import cz.mg.test.exceptions.AssertException;

import java.nio.file.Path;

public @Test class DirectoryServiceTest {
    public static void main(String[] args) {
        System.out.print("Running " + DirectoryServiceTest.class.getSimpleName() + " ... ");

        DirectoryServiceTest test = new DirectoryServiceTest();
        test.testEmpty();
        test.testSingle();
        test.testMultiple();

        System.out.println("OK");
    }

    private final @Service DirectoryService directoryService = DirectoryService.getInstance();

    private void testEmpty() {
        Progress nodeProgress = new Progress("test");
        directoryService.forEachNode(null, node -> {
            throw new AssertException("No consumer should be called.");
        }, nodeProgress);
        Assert.assertEquals(0L, nodeProgress.getLimit());
        Assert.assertEquals(0L, nodeProgress.getValue());

        Progress fileProgress = new Progress("test");
        directoryService.forEachFile(null, node -> {
            throw new AssertException("No consumer should be called.");
        }, fileProgress);
        Assert.assertEquals(0L, fileProgress.getLimit());
        Assert.assertEquals(0L, fileProgress.getValue());

        Progress directoryProgress = new Progress("test");
        directoryService.forEachDirectory(null, node -> {
            throw new AssertException("No consumer should be called.");
        }, directoryProgress);
        Assert.assertEquals(0L, directoryProgress.getLimit());
        Assert.assertEquals(0L, directoryProgress.getValue());
    }

    private void testSingle() {
        File file = createFile(Path.of("/f"));
        Directory directory = createDirectory(Path.of("/d"), file);

        Progress nodeProgress = new Progress("test");
        directoryService.forEachNode(directory, n -> n.setPath(Path.of("/x")), nodeProgress);

        Assert.assertEquals(Path.of("/x"), file.getPath());
        Assert.assertEquals(Path.of("/x"), directory.getPath());
        Assert.assertEquals(0L, nodeProgress.getLimit()); // set by parent service
        Assert.assertEquals(2L, nodeProgress.getValue());

        Progress fileProgress = new Progress("test");
        directoryService.forEachFile(directory, f -> f.setPath(Path.of("/f1")), fileProgress);

        Assert.assertEquals(Path.of("/f1"), file.getPath());
        Assert.assertEquals(Path.of("/x"), directory.getPath());
        Assert.assertEquals(0L, fileProgress.getLimit()); // set by parent service
        Assert.assertEquals(2L, fileProgress.getValue());

        Progress directoryProgress = new Progress("test");
        directoryService.forEachDirectory(directory, d -> d.setPath(Path.of("/d1")), directoryProgress);

        Assert.assertEquals(Path.of("/f1"), file.getPath());
        Assert.assertEquals(Path.of("/d1"), directory.getPath());
        Assert.assertEquals(0L, directoryProgress.getLimit()); // set by parent service
        Assert.assertEquals(2L, directoryProgress.getValue());
    }

    private void testMultiple() {
        File file = createFile(Path.of("/f"));
        Directory directory = createDirectory(Path.of("/d"), file);
        File secondFile = createFile(Path.of("/ff"));
        Directory secondDirectory = createDirectory(Path.of("/dd"), secondFile);
        directory.getDirectories().addLast(secondDirectory);

        Progress nodeProgress = new Progress("test");
        directoryService.forEachNode(directory, n -> n.setPath(Path.of("/x")), nodeProgress);

        Assert.assertEquals(Path.of("/x"), file.getPath());
        Assert.assertEquals(Path.of("/x"), directory.getPath());
        Assert.assertEquals(Path.of("/x"), secondFile.getPath());
        Assert.assertEquals(Path.of("/x"), secondDirectory.getPath());
        Assert.assertEquals(0L, nodeProgress.getLimit()); // set by parent service
        Assert.assertEquals(4L, nodeProgress.getValue());

        Progress fileProgress = new Progress("test");
        directoryService.forEachFile(directory, f -> f.setPath(Path.of("/f1")), fileProgress);

        Assert.assertEquals(Path.of("/f1"), file.getPath());
        Assert.assertEquals(Path.of("/x"), directory.getPath());
        Assert.assertEquals(Path.of("/f1"), secondFile.getPath());
        Assert.assertEquals(Path.of("/x"), secondDirectory.getPath());
        Assert.assertEquals(0L, fileProgress.getLimit()); // set by parent service
        Assert.assertEquals(4L, fileProgress.getValue());

        Progress directoryProgress = new Progress("test");
        directoryService.forEachDirectory(directory, d -> d.setPath(Path.of("/d1")), directoryProgress);

        Assert.assertEquals(Path.of("/f1"), file.getPath());
        Assert.assertEquals(Path.of("/d1"), directory.getPath());
        Assert.assertEquals(Path.of("/f1"), secondFile.getPath());
        Assert.assertEquals(Path.of("/d1"), secondDirectory.getPath());
        Assert.assertEquals(0L, directoryProgress.getLimit()); // set by parent service
        Assert.assertEquals(4L, directoryProgress.getValue());
    }

    private @Mandatory Directory createDirectory(@Mandatory Path path, @Mandatory File file) {
        Directory directory = new Directory();
        directory.setPath(path);
        directory.getFiles().addLast(file);
        return directory;
    }

    private @Mandatory File createFile(@Mandatory Path path) {
        File file = new File();
        file.setPath(path);
        return file;
    }
}
