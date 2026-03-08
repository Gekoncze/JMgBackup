package cz.mg.backup.services;

import cz.mg.annotations.classes.Test;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.backup.Configuration;
import cz.mg.test.Assertions;

import java.nio.file.Files;
import java.nio.file.Path;

public @Test class DirectoryWriterTest {
    private static final @Mandatory Path PATH = Configuration.getRoot(DirectoryWriterTest.class);
    private static final @Mandatory Path MISSING_DIRECTORY_PATH = PATH.resolve("missing");
    private static final @Mandatory Path NESTED_DIRECTORY_PATH = PATH.resolve("one").resolve("two").resolve("three");
    private static final @Mandatory Path EXISTING_DIRECTORY_PATH = PATH.resolve("existing");

    public static void main(String[] args) {
        System.out.print("Running " + DirectoryWriterTest.class.getSimpleName() + " ... ");

        DirectoryWriterTest test = new DirectoryWriterTest();
        test.testCreateDirectory();
        test.testCreateDirectories();
        test.testExistingDirectory();
        test.testNull();

        System.out.println("OK");
    }

    private final @Mandatory DirectoryWriter directoryWriter = DirectoryWriter.getInstance();

    private void testCreateDirectory() {
        Assertions.assertThat(Files.exists(MISSING_DIRECTORY_PATH))
            .withMessage("Precondition is not met.")
            .isEqualTo(false);

        try {
            directoryWriter.createDirectories(MISSING_DIRECTORY_PATH);

            Assertions.assertThat(Files.exists(MISSING_DIRECTORY_PATH))
                .withMessage("Missing directory should be created.")
                .isEqualTo(true);
        } finally {
            if (Files.exists(MISSING_DIRECTORY_PATH)) {
                Assertions.assertThatCode(() -> Files.delete(MISSING_DIRECTORY_PATH))
                    .withMessage("Test cleanup failed.")
                    .doesNotThrowAnyException();
            }
        }
    }

    private void testCreateDirectories() {
        Assertions.assertThat(Files.exists(NESTED_DIRECTORY_PATH))
            .withMessage("Precondition is not met.")
            .isEqualTo(false);

        Assertions.assertThat(Files.exists(NESTED_DIRECTORY_PATH.getParent()))
            .withMessage("Precondition is not met.")
            .isEqualTo(false);

        Assertions.assertThat(Files.exists(NESTED_DIRECTORY_PATH.getParent().getParent()))
            .withMessage("Precondition is not met.")
            .isEqualTo(false);

        try {
            directoryWriter.createDirectories(NESTED_DIRECTORY_PATH);

            Assertions.assertThat(Files.exists(NESTED_DIRECTORY_PATH))
                .withMessage("Missing nested directory should be created.")
                .isEqualTo(true);
        } finally {
            if (Files.exists(NESTED_DIRECTORY_PATH)) {
                Assertions.assertThatCode(() -> Files.delete(NESTED_DIRECTORY_PATH))
                    .withMessage("Test cleanup failed.")
                    .doesNotThrowAnyException();
            }

            if (Files.exists(NESTED_DIRECTORY_PATH.getParent())) {
                Assertions.assertThatCode(() -> Files.delete(NESTED_DIRECTORY_PATH.getParent()))
                    .withMessage("Test cleanup failed.")
                    .doesNotThrowAnyException();
            }

            if (Files.exists(NESTED_DIRECTORY_PATH.getParent().getParent())) {
                Assertions.assertThatCode(() -> Files.delete(NESTED_DIRECTORY_PATH.getParent().getParent()))
                    .withMessage("Test cleanup failed.")
                    .doesNotThrowAnyException();
            }
        }
    }

    private void testExistingDirectory() {
        Assertions.assertThatCode(() -> directoryWriter.createDirectories(EXISTING_DIRECTORY_PATH))
            .withMessage("Creation of existing directory should be skipped.")
            .doesNotThrowAnyException();

        Assertions.assertThat(Files.exists(EXISTING_DIRECTORY_PATH))
            .withMessage("Existing directory should still exist.")
            .isEqualTo(true);
    }

    private void testNull() {
        Assertions.assertThatCode(() -> directoryWriter.createDirectories(null))
            .withMessage("Null path should be skipped.")
            .doesNotThrowAnyException();
    }
}
