package cz.mg.backup.services;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.classes.Test;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.backup.components.Progress;
import cz.mg.backup.entities.Algorithm;
import cz.mg.backup.test.common.Common;
import cz.mg.test.Assert;
import cz.mg.test.Assertions;
import cz.mg.test.exceptions.AssertException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;

public @Test class FileCopyTest {
    private static final @Mandatory Path SOURCE_FILE = Common.FLYING_AKI_PATH;
    private static final @Mandatory Path SOURCE_DIRECTORY = SOURCE_FILE.getParent();
    private static final @Mandatory Path TARGET_DIRECTORY = SOURCE_DIRECTORY.resolve("copy");
    private static final @Mandatory Path TARGET_FILE = TARGET_DIRECTORY.resolve(SOURCE_FILE.getFileName());

    public static void main(String[] args) {
        System.out.print("Running " + FileCopyTest.class.getSimpleName() + " ... ");

        FileCopyTest test = new FileCopyTest();
        test.testCopy();
        test.testValidateSourceFileType();
        test.testValidateMissingSourceFile();

        System.out.println("OK");
    }

    private final @Service FileCopy fileCopy = FileCopy.getInstance();

    private void testCopy() {
        Assert.assertEquals(true, Files.exists(SOURCE_DIRECTORY));
        Assert.assertEquals(true, Files.exists(SOURCE_FILE));
        Assert.assertEquals(false, Files.exists(TARGET_DIRECTORY));
        Assert.assertEquals(false, Files.exists(TARGET_FILE));
        Assert.assertNotEquals(SOURCE_FILE, TARGET_FILE);
        Assert.assertNotEquals(SOURCE_DIRECTORY, TARGET_DIRECTORY);
        Assert.assertNotEquals(SOURCE_FILE, SOURCE_DIRECTORY);
        Assert.assertNotEquals(TARGET_FILE, TARGET_DIRECTORY);

        try {
            fileCopy.copy(SOURCE_FILE, TARGET_FILE, Algorithm.SHA256, new Progress());
            Assert.assertEquals(true, Files.exists(TARGET_DIRECTORY));
            Assert.assertEquals(true, Files.exists(TARGET_FILE));
            Assert.assertEquals(Files.size(SOURCE_FILE), Files.size(TARGET_FILE));

            BasicFileAttributes sourceAttributes = Files.readAttributes(SOURCE_FILE, BasicFileAttributes.class);
            BasicFileAttributes targetAttributes = Files.readAttributes(TARGET_FILE, BasicFileAttributes.class);
            // creation time cannot be copied on linux
            Assert.assertEquals(sourceAttributes.lastModifiedTime(), targetAttributes.lastModifiedTime());
            Assert.assertEquals(sourceAttributes.lastAccessTime(), targetAttributes.lastAccessTime());

            Assertions.assertThatCode(() -> fileCopy.copy(SOURCE_FILE, TARGET_FILE, Algorithm.SHA256, new Progress()))
                .withMessage("Missing validation for existing target file.")
                .throwsException(IllegalArgumentException.class);
        } catch (IOException e) {
            throw new AssertException("Unexpected file system exception.", e);
        } finally {
            if (Files.exists(TARGET_FILE)) {
                Assertions.assertThatCode(() -> Files.delete(TARGET_FILE))
                    .withMessage("Test cleanup failed!")
                    .doesNotThrowAnyException();
            }

            if (Files.exists(TARGET_DIRECTORY)) {
                Assertions.assertThatCode(() -> Files.delete(TARGET_DIRECTORY))
                    .withMessage("Test cleanup failed!")
                    .doesNotThrowAnyException();
            }
        }
    }

    private void testValidateSourceFileType() {
        try {
            Path source = SOURCE_FILE.getParent();
            Assert.assertEquals(true, Files.isDirectory(source));
            Assertions.assertThatCode(() -> fileCopy.copy(source, TARGET_FILE, Algorithm.SHA256, new Progress()))
                .withMessage("Missing validation for source file type.")
                .throwsException(IllegalArgumentException.class);
        } finally {
            if (Files.exists(TARGET_FILE)) {
                Assertions.assertThatCode(() -> Files.delete(TARGET_FILE))
                    .withMessage("Test cleanup failed!")
                    .doesNotThrowAnyException();
            }
        }
    }

    private void testValidateMissingSourceFile() {
        try {
            Path source = SOURCE_FILE.getParent().resolve(SOURCE_FILE.getFileName().toString() + "x");
            Assert.assertEquals(false, Files.exists(source));
            Assertions.assertThatCode(() -> fileCopy.copy(source, TARGET_FILE, Algorithm.SHA256, new Progress()))
                .withMessage("Missing validation for missing source file.")
                .throwsException(IllegalArgumentException.class);
        } finally {
            if (Files.exists(TARGET_FILE)) {
                Assertions.assertThatCode(() -> Files.delete(TARGET_FILE))
                    .withMessage("Test cleanup failed!")
                    .doesNotThrowAnyException();
            }
        }
    }
}
