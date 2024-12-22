package cz.mg.backup.services;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.classes.Test;
import cz.mg.annotations.requirement.Mandatory;
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
        directoryService.forEachNode(null, node -> {
            throw new AssertException("No consumer should be called.");
        });

        directoryService.forEachFile(null, node -> {
            throw new AssertException("No consumer should be called.");
        });

        directoryService.forEachDirectory(null, node -> {
            throw new AssertException("No consumer should be called.");
        });
    }

    private void testSingle() {
        File file = createFile(Path.of("/f"));
        Directory directory = createDirectory(Path.of("/d"), file);

        directoryService.forEachNode(directory, n -> n.setPath(Path.of("/x")));

        Assert.assertEquals(Path.of("/x"), file.getPath());
        Assert.assertEquals(Path.of("/x"), directory.getPath());

        directoryService.forEachFile(directory, f -> f.setPath(Path.of("/f1")));

        Assert.assertEquals(Path.of("/f1"), file.getPath());
        Assert.assertEquals(Path.of("/x"), directory.getPath());

        directoryService.forEachDirectory(directory, d -> d.setPath(Path.of("/d1")));

        Assert.assertEquals(Path.of("/f1"), file.getPath());
        Assert.assertEquals(Path.of("/d1"), directory.getPath());
    }

    private void testMultiple() {
        File file = createFile(Path.of("/f"));
        Directory directory = createDirectory(Path.of("/d"), file);
        File secondFile = createFile(Path.of("/ff"));
        Directory secondDirectory = createDirectory(Path.of("/dd"), secondFile);
        directory.getDirectories().addLast(secondDirectory);

        directoryService.forEachNode(directory, n -> n.setPath(Path.of("/x")));

        Assert.assertEquals(Path.of("/x"), file.getPath());
        Assert.assertEquals(Path.of("/x"), directory.getPath());
        Assert.assertEquals(Path.of("/x"), secondFile.getPath());
        Assert.assertEquals(Path.of("/x"), secondDirectory.getPath());

        directoryService.forEachFile(directory, f -> f.setPath(Path.of("/f1")));

        Assert.assertEquals(Path.of("/f1"), file.getPath());
        Assert.assertEquals(Path.of("/x"), directory.getPath());
        Assert.assertEquals(Path.of("/f1"), secondFile.getPath());
        Assert.assertEquals(Path.of("/x"), secondDirectory.getPath());

        directoryService.forEachDirectory(directory, d -> d.setPath(Path.of("/d1")));

        Assert.assertEquals(Path.of("/f1"), file.getPath());
        Assert.assertEquals(Path.of("/d1"), directory.getPath());
        Assert.assertEquals(Path.of("/f1"), secondFile.getPath());
        Assert.assertEquals(Path.of("/d1"), secondDirectory.getPath());
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
