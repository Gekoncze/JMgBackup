package cz.mg.backup.services;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.classes.Test;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.backup.entities.Directory;
import cz.mg.backup.entities.File;
import cz.mg.test.Assert;

import java.nio.file.Path;

public @Test class DirectorySearchTest {
    public static void main(String[] args) {
        System.out.print("Running " + DirectorySearchTest.class.getSimpleName() + " ... ");

        DirectorySearchTest test = new DirectorySearchTest();
        test.testEmpty();
        test.testSingle();
        test.testMultiple();

        System.out.println("OK");
    }

    private final @Service DirectorySearch search = DirectorySearch.getInstance();

    private void testEmpty() {
        Assert.assertNull(search.find(null, Path.of("/f")));
    }

    private void testSingle() {
        File file = createFile(Path.of("/f"));
        Directory directory = createDirectory(Path.of("/d"), file);

        Assert.assertSame(file, search.find(directory, Path.of("/f")));
        Assert.assertSame(directory, search.find(directory, Path.of("/d")));
        Assert.assertNull(search.find(directory, Path.of("/x")));
        Assert.assertNull(search.find(directory, Path.of("")));
    }

    private void testMultiple() {
        File file = createFile(Path.of("/f"));
        Directory directory = createDirectory(Path.of("/d"), file);
        File secondFile = createFile(Path.of("/ff"));
        Directory secondDirectory = createDirectory(Path.of("/dd"), secondFile);
        directory.getDirectories().addLast(secondDirectory);

        Assert.assertSame(file, search.find(directory, Path.of("/f")));
        Assert.assertSame(directory, search.find(directory, Path.of("/d")));
        Assert.assertSame(secondFile, search.find(directory, Path.of("/ff")));
        Assert.assertSame(secondDirectory, search.find(directory, Path.of("/dd")));
        Assert.assertNull(search.find(directory, Path.of("/x")));
        Assert.assertNull(search.find(directory, Path.of("")));
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
