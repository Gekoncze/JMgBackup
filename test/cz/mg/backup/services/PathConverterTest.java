package cz.mg.backup.services;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.classes.Test;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.backup.components.Progress;
import cz.mg.backup.entities.Directory;
import cz.mg.backup.entities.File;
import cz.mg.test.Assert;
import cz.mg.test.Assertions;

import java.nio.file.Path;

public @Test class PathConverterTest {
    public static void main(String[] args) {
        System.out.print("Running " + PathConverterTest.class.getSimpleName() + " ... ");

        PathConverterTest test = new PathConverterTest();
        test.testEmptyPaths();
        test.testFileElsewhereValidation();
        test.testRelativePath();
        test.testComputeRelativePaths();

        System.out.println("OK");
    }

    private final @Service PathConverter pathConverter = PathConverter.getInstance();

    private void testEmptyPaths() {
        testLegal("", "", "");
    }

    private void testFileElsewhereValidation() {
        testIllegal("", "foo");
        testIllegal("Aki", "foo");
        testIllegal("bar/Aki", "foo");
        testIllegal("bar/foo/Aki", "foo");
        testIllegal("foo/Aki", "foo/a");
        testIllegal("foo/Aki", "foo/a/b");
        testIllegal("foo/a/Aki", "foo/a/b/c");
    }

    private void testRelativePath() {
        testLegal("Aki", "", "Aki");
        testLegal("foo", "foo", "");
        testLegal("foo/Aki", "foo", "Aki");
        testLegal("foo/bar/Aki", "foo/bar", "Aki");
        testLegal("foo/bar/Aki", "foo", "bar/Aki");
        testLegal("foo/a/b/c/Aki", "foo", "a/b/c/Aki");
        testLegal("foo/a/b/Aki", "foo/a", "b/Aki");
        testLegal("foo/bar/Aki", "foo", "bar/Aki");
        testLegal("1/2/3/4/5/Aki", "1/2/3", "4/5/Aki");
    }

    private void testComputeRelativePaths() {
        File firstFile = new File();
        firstFile.setPath(Path.of("/foo/bar/Fluffy.bmp"));

        File secondFile = new File();
        secondFile.setPath(Path.of("/foo/bar/67/69"));

        Directory subSubDirectory = new Directory();
        subSubDirectory.setPath(Path.of("/foo/bar/67/fun"));

        Directory subDirectory = new Directory();
        subDirectory.setPath(Path.of("/foo/bar/67"));
        subDirectory.getFiles().addLast(secondFile);
        subDirectory.getDirectories().addLast(subSubDirectory);

        Directory directory = new Directory();
        directory.setPath(Path.of("/foo/bar"));
        directory.getFiles().addLast(firstFile);
        directory.getDirectories().addLast(subDirectory);
        directory.getProperties().setTotalFileCount(2);
        directory.getProperties().setTotalDirectoryCount(2);

        Progress progress = new Progress();
        pathConverter.computeRelativePaths(directory, progress);

        Assert.assertEquals(Path.of("Fluffy.bmp"), firstFile.getRelativePath());
        Assert.assertEquals(Path.of("67/69"), secondFile.getRelativePath());
        Assert.assertEquals(Path.of(""), directory.getRelativePath());
        Assert.assertEquals(Path.of("67"), subDirectory.getRelativePath());
        Assert.assertEquals(Path.of("67/fun"), subSubDirectory.getRelativePath());
        Assert.assertEquals(5, progress.getLimit());
        Assert.assertEquals(5, progress.getValue());
    }

    private void testIllegal(
        @Mandatory String filePath,
        @Mandatory String sourceDirectoryPath
    ) {
        testIllegalCase(filePath, sourceDirectoryPath);
        testIllegalCase("/" + filePath, "/" + sourceDirectoryPath);
    }

    private void testIllegalCase(
        @Mandatory String filePath,
        @Mandatory String directoryPath
    ) {
        Assertions.assertThatCode(() -> pathConverter.toRelativePath(
                Path.of(filePath),
                Path.of(directoryPath)
            ))
            .withMessage("Invalid arguments should be rejected.")
            .throwsException(IllegalArgumentException.class);
    }

    private void testLegal(
        @Mandatory String filePath,
        @Mandatory String directoryPath,
        @Mandatory String expectedPath
    ) {
        testLegalCase(filePath, directoryPath, expectedPath);
        testLegalCase("/" + filePath, "/" + directoryPath, expectedPath);
    }

    private void testLegalCase(
        @Mandatory String filePath,
        @Mandatory String directoryPath,
        @Mandatory String expectedPath
    ) {
        String result = pathConverter.toRelativePath(
            Path.of(filePath),
            Path.of(directoryPath)
        ).toString();

        Assertions.assertThat(result)
            .withMessage("Unexpected path.")
            .isEqualTo(expectedPath);
    }
}
