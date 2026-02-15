package cz.mg.backup.services;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.classes.Test;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.annotations.requirement.Optional;
import cz.mg.backup.entities.*;
import cz.mg.backup.test.TestFactory;
import cz.mg.backup.test.TestProgress;
import cz.mg.test.Assertions;

import java.nio.file.Path;
import java.util.function.Consumer;

public @Test class DirectorySearchTest {
    public static void main(String[] args) {
        System.out.print("Running " + DirectorySearchTest.class.getSimpleName() + " ... ");

        DirectorySearchTest test = new DirectorySearchTest();
        test.testNoDirectory();
        test.testSimpleSearch();
        test.testNestedSearch();
        test.testTwoDirectorySearch();

        System.out.println("OK");
    }

    private final @Service DirectorySearch search = DirectorySearch.getInstance();
    private final @Service TestFactory f = TestFactory.getInstance();

    private void testNoDirectory() {
        testSingleDirectory(null, null, null, p -> p.verifySkip());
        testSingleDirectory(null, Path.of("/f"), null, p -> p.verifySkip());
        testTwoDirectories(null, null, Path.of("first"), null, p -> p.verifySkip());
        testTwoDirectories(null, null, null, null, p -> p.verifySkip());
    }

    private void testSimpleSearch() {
        File file = f.file("f");
        Directory directory = f.directory("d", file);

        testSingleDirectory(directory, Path.of("d/f"), file, p -> p.verify(2L, 2L));
        testSingleDirectory(directory, Path.of("d"), directory, p -> p.verify(2L, 2L));
        testSingleDirectory(directory, Path.of("x"), null, p -> p.verify(2L, 2L));
        testSingleDirectory(directory, Path.of(""), null, p -> p.verify(2L, 2L));
        testSingleDirectory(directory, null, null, p -> p.verifySkip());
    }

    private void testNestedSearch() {
        File file = f.file("f");
        File nestedFile = f.file("ff");
        Directory nestedDirectory = f.directory("dd", nestedFile);
        Directory directory = f.directory("d", file, nestedDirectory);

        testSingleDirectory(directory, Path.of("d/f"), file, p -> p.verify(4L, 4L));
        testSingleDirectory(directory, Path.of("d"), directory, p -> p.verify(4L, 4L));
        testSingleDirectory(directory, Path.of("d/dd/ff"), nestedFile, p -> p.verify(4L, 4L));
        testSingleDirectory(directory, Path.of("d/dd"), nestedDirectory, p -> p.verify(4L, 4L));
        testSingleDirectory(directory, Path.of("x"), null, p -> p.verify(4L, 4L));
        testSingleDirectory(directory, Path.of(""), null, p -> p.verify(4L, 4L));
        testSingleDirectory(directory, null, null, p -> p.verifySkip());
    }

    private void testTwoDirectorySearch() {
        File firstFile = f.file("foo");
        Directory firstDirectory = f.directory("first", firstFile);

        File secondFile = f.file("foo");
        Directory secondDirectory = f.directory("second", secondFile);

        testTwoDirectories(firstDirectory, secondDirectory, Path.of("first/foo"), firstFile, p-> p.verify());
        testTwoDirectories(firstDirectory, secondDirectory, Path.of("first"), firstDirectory, p-> p.verify());
        testTwoDirectories(firstDirectory, secondDirectory, Path.of("second/foo"), secondFile, p-> p.verify());
        testTwoDirectories(firstDirectory, secondDirectory, Path.of("second"), secondDirectory, p-> p.verify());
        testTwoDirectories(firstDirectory, secondDirectory, Path.of("x"), null, p-> p.verify());
        testTwoDirectories(firstDirectory, secondDirectory, Path.of(""), null, p-> p.verify());
        testTwoDirectories(firstDirectory, null, Path.of("first/foo"), firstFile, p-> p.verify());
        testTwoDirectories(null, secondDirectory, Path.of("second/foo"), secondFile, p-> p.verify());
        testTwoDirectories(firstDirectory, secondDirectory, null, null, p -> p.verifySkip());
    }

    private void testSingleDirectory(
        @Optional Directory directory,
        @Optional Path wanted,
        @Optional Node expectation,
        @Mandatory Consumer<TestProgress> progressVerification
    ) {
        TestProgress progress = new TestProgress();
        Node reality = search.find(directory, wanted, progress);

        Assertions.assertThat(reality)
                .withFormatFunction(n -> n.getPath().toString())
                .isSameAs(expectation);

        progressVerification.accept(progress);
    }

    private void testTwoDirectories(
        @Optional Directory firstDirectory,
        @Optional Directory secondDirectory,
        @Optional Path wanted,
        @Optional Node expectation,
        @Mandatory Consumer<TestProgress> progressVerification
    ) {
        TestProgress progress = new TestProgress();
        Node reality = search.find(firstDirectory, secondDirectory, wanted, progress);

        Assertions.assertThat(reality)
            .withFormatFunction(n -> n.getPath().toString())
            .isSameAs(expectation);

        progressVerification.accept(progress);
    }
}
