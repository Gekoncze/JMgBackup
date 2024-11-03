package cz.mg.backup.services;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.classes.Test;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.backup.components.Progress;
import cz.mg.backup.entities.Directory;
import cz.mg.backup.entities.File;
import cz.mg.backup.entities.Node;
import cz.mg.test.Assert;

import java.nio.file.Path;

public @Test class DirectorySortTest {
    public static void main(String[] args) {
        System.out.print("Running " + DirectorySortTest.class.getSimpleName() + " ... ");

        DirectorySortTest test = new DirectorySortTest();
        test.testSort();

        System.out.println("OK");
    }

    private final @Service DirectorySort sort = DirectorySort.getInstance();

    private void testSort() {
        Directory directory = new Directory();
        directory.getDirectories().addLast(createDirectory(Path.of("Y", "AA")));
        directory.getDirectories().addLast(createDirectory(Path.of("Z", "A")));
        directory.getDirectories().addLast(createDirectory(Path.of("W", "BB")));
        directory.getDirectories().addLast(createDirectory(Path.of("y", "B")));
        directory.getFiles().addLast(createFile(Path.of("W", "BB")));
        directory.getFiles().addLast(createFile(Path.of("Y", "AA")));
        directory.getFiles().addLast(createFile(Path.of("y", "B")));
        directory.getFiles().addLast(createFile(Path.of("Z", "A")));

        sort.sort(directory, new Progress("Test"));

        checkName("A", directory.getDirectories().get(0));
        checkName("AA", directory.getDirectories().get(1));
        checkName("B", directory.getDirectories().get(2));
        checkName("BB", directory.getDirectories().get(3));
        checkName("A", directory.getFiles().get(0));
        checkName("AA", directory.getFiles().get(1));
        checkName("B", directory.getFiles().get(2));
        checkName("BB", directory.getFiles().get(3));
    }

    private void checkName(@Mandatory String expectation, @Mandatory Node node) {
        Assert.assertEquals(expectation, node.getPath().getFileName().toString());
    }

    private @Mandatory Directory createDirectory(@Mandatory Path path) {
        Directory directory = new Directory();
        directory.setPath(path);
        return directory;
    }

    private @Mandatory File createFile(@Mandatory Path path) {
        File file = new File();
        file.setPath(path);
        return file;
    }
}
