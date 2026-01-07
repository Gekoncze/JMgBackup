package cz.mg.backup.services;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.classes.Test;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.test.Assertions;

import java.nio.file.Path;

public @Test class PathConverterTest {
    public static void main(String[] args) {
        System.out.print("Running " + PathConverterTest.class.getSimpleName() + " ... ");

        PathConverterTest test = new PathConverterTest();
        test.testAllEmptyValidation();
        test.testFileEmptyValidation();
        test.testFileElsewhereValidation();
        test.testRoot();
        test.testNested();

        System.out.println("OK");
    }

    private final @Service PathConverter pathConverter = PathConverter.getInstance();

    private void testAllEmptyValidation() {
        testIllegal("", "", "");
    }

    private void testFileEmptyValidation() {
        testIllegal("", "foo", "bar");
        testIllegal("", "", "bar");
        testIllegal("", "foo", "");
    }

    private void testFileElsewhereValidation() {
        testIllegal("Aki", "foo", "bar");
        testIllegal("bar/Aki", "foo", "bar");
        testIllegal("bar/foo/Aki", "foo", "bar");
        testIllegal("foo/Aki", "foo/a", "bar");
        testIllegal("foo/Aki", "foo/a/b", "bar");
        testIllegal("foo/a/Aki", "foo/a/b/c", "bar");
    }

    private void testRoot() {
        testLegal("Aki", "", "", "Aki");
        testLegal("Aki", "", "bar", "bar/Aki");
        testLegal("foo/Aki", "foo", "", "Aki");
    }

    private void testNested() {
        testLegal("foo/Aki", "foo", "bar", "bar/Aki");
        testLegal("foo/a/Aki", "foo/a", "bar/1", "bar/1/Aki");
        testLegal("foo/a/Aki", "foo", "bar", "bar/a/Aki");
        testLegal("foo/a/b/c/Aki", "foo", "bar", "bar/a/b/c/Aki");
        testLegal("foo/a/Aki", "foo/a", "foo/b", "foo/b/Aki");
        testLegal("foo/a/b/Aki", "foo/a", "bar/a", "bar/a/b/Aki");
        testLegal("foo/a/b/Aki", "foo/a", "bar", "bar/b/Aki");
        testLegal("foo/b/Aki", "foo", "bar/a", "bar/a/b/Aki");
    }

    private void testIllegal(
        @Mandatory String filePath,
        @Mandatory String sourceDirectoryPath,
        @Mandatory String targetDirectoryPath
    ) {
        testIllegalCase(filePath, sourceDirectoryPath, targetDirectoryPath);
        testIllegalCase("/" + filePath, "/" + sourceDirectoryPath, "/" + targetDirectoryPath);
    }

    private void testIllegalCase(
        @Mandatory String filePath,
        @Mandatory String sourceDirectoryPath,
        @Mandatory String targetDirectoryPath
    ) {
        Assertions.assertThatCode(() -> pathConverter.sourcePathToTargetPath(
                Path.of(filePath),
                Path.of(sourceDirectoryPath),
                Path.of(targetDirectoryPath)
            ))
            .withMessage("Invalid arguments should be rejected.")
            .throwsException(IllegalArgumentException.class);
    }

    private void testLegal(
        @Mandatory String filePath,
        @Mandatory String sourceDirectoryPath,
        @Mandatory String targetDirectoryPath,
        @Mandatory String expectedPath
    ) {
        testLegalCase(filePath, sourceDirectoryPath, targetDirectoryPath, expectedPath);
        testLegalCase("/" + filePath, "/" + sourceDirectoryPath, "/" + targetDirectoryPath, "/" + expectedPath);
        testLegalCase(filePath + ".png", sourceDirectoryPath, targetDirectoryPath, expectedPath + ".png");
    }

    private void testLegalCase(
        @Mandatory String filePath,
        @Mandatory String sourceDirectoryPath,
        @Mandatory String targetDirectoryPath,
        @Mandatory String expectedPath
    ) {
        String result = pathConverter.sourcePathToTargetPath(
            Path.of(filePath),
            Path.of(sourceDirectoryPath),
            Path.of(targetDirectoryPath)
        ).toString();

        Assertions.assertThat(result)
            .withMessage("Unexpected path.")
            .isEqualTo(expectedPath);
    }
}
