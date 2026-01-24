package cz.mg.backup.services;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.classes.Test;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.annotations.requirement.Optional;
import cz.mg.backup.components.Progress;
import cz.mg.backup.entities.*;
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
    private final @Service StatisticsCounter statisticsCounter = StatisticsCounter.getInstance();

    private void testEmpty() {
        test(null, Path.of("/f"), null, 0L);
    }

    private void testSingle() {
        File file = createFile(Path.of("/d/f"));
        Directory directory = createDirectory(Path.of("/d"), file);

        test(directory, Path.of("/d/f"), file, 2L);
        test(directory, Path.of("/d"), directory, 2L);
        test(directory, Path.of("/x"), null, 2L);
        test(directory, Path.of(""), null, 2L);
    }

    private void testMultiple() {
        File file = createFile(Path.of("/d/f"));
        Directory directory = createDirectory(Path.of("/d"), file);
        File secondFile = createFile(Path.of("/d/dd/ff"));
        Directory secondDirectory = createDirectory(Path.of("/d/dd"), secondFile);
        directory.getDirectories().addLast(secondDirectory);

        test(directory, Path.of("/d/f"), file, 4L);
        test(directory, Path.of("/d"), directory, 4L);
        test(directory, Path.of("/d/dd/ff"), secondFile, 4L);
        test(directory, Path.of("/d/dd"), secondDirectory, 4L);
        test(directory, Path.of("/x"), null, 4L);
        test(directory, Path.of(""), null, 4L);
    }

    private void test(
        @Optional Directory directory,
        @Mandatory Path wanted,
        @Optional Node expectation,
        long expectedCount
    ) {
        Progress progress = new Progress();
        statisticsCounter.count(directory, new Progress());
        Node reality = search.find(directory, wanted, progress);
        Assert.assertSame(expectation, reality);
        Assert.assertEquals(expectedCount, progress.getLimit());
        Assert.assertEquals(expectedCount, progress.getValue());
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
        file.getProperties().setSize(1L);
        return file;
    }
}
