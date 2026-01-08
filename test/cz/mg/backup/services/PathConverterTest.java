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
        test.testConvert();

        System.out.println("OK");
    }

    private final @Service PathConverter pathConverter = PathConverter.getInstance();

    private void testAllEmptyValidation() {
        testIllegal("", "");
    }

    private void testFileEmptyValidation() {
        testIllegal("", "foo");
    }

    private void testFileElsewhereValidation() {
        testIllegal("Aki", "foo");
        testIllegal("bar/Aki", "foo");
        testIllegal("bar/foo/Aki", "foo");
        testIllegal("foo/Aki", "foo/a");
        testIllegal("foo/Aki", "foo/a/b");
        testIllegal("foo/a/Aki", "foo/a/b/c");
    }

    private void testConvert() {
        testLegal("Aki", "", "Aki");
        testLegal("foo/Aki", "foo", "Aki");
        testLegal("foo/bar/Aki", "foo/bar", "Aki");
        testLegal("foo/bar/Aki", "foo", "bar/Aki");
        testLegal("foo/a/b/c/Aki", "foo", "a/b/c/Aki");
        testLegal("foo/a/b/Aki", "foo/a", "b/Aki");
        testLegal("foo/bar/Aki", "foo", "bar/Aki");
        testLegal("1/2/3/4/5/Aki", "1/2/3", "4/5/Aki");
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
        testLegalCase(filePath + ".png", directoryPath, expectedPath + ".png");
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
