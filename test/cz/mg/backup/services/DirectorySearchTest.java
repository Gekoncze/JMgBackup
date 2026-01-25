package cz.mg.backup.services;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.classes.Test;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.annotations.requirement.Optional;
import cz.mg.backup.entities.*;
import cz.mg.backup.test.TestFactory;
import cz.mg.backup.test.TestProgress;
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
    private final @Service TestFactory f = TestFactory.getInstance();

    private void testEmpty() {
        test(null, Path.of("/f"), null, 0L);
    }

    private void testSingle() {
        File file = f.file("/d/f");
        Directory directory = f.directory("/d", file);

        test(directory, Path.of("/d/f"), file, 2L);
        test(directory, Path.of("/d"), directory, 2L);
        test(directory, Path.of("/x"), null, 2L);
        test(directory, Path.of(""), null, 2L);
    }

    private void testMultiple() {
        File file = f.file("/d/f");
        File secondFile = f.file("/d/dd/ff");
        Directory secondDirectory = f.directory("/d/dd", secondFile);
        Directory directory = f.directory("/d", file, secondDirectory);

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
        TestProgress progress = new TestProgress();
        Node reality = search.find(directory, wanted, progress);
        Assert.assertSame(expectation, reality);
        progress.verify(expectedCount, expectedCount);
    }
}
