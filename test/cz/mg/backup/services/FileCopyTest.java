package cz.mg.backup.services;

import cz.mg.annotations.classes.Test;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.backup.Configuration;
import cz.mg.backup.entities.File;
import cz.mg.backup.exceptions.FileSystemException;
import cz.mg.backup.test.TestProgress;
import cz.mg.test.Assert;
import cz.mg.test.Assertions;

import java.nio.file.Files;
import java.nio.file.Path;

public @Test class FileCopyTest {
    private static final @Mandatory Path PATH = Configuration.getRoot(FileCopyTest.class);
    private static final @Mandatory Path SOURCE_PATH = PATH.resolve("original.txt");
    private static final @Mandatory Path COPY_PATH = PATH.resolve("copy.txt");
    private static final @Mandatory Path MISSING_PATH = PATH.resolve("x.missing");

    public static void main(String[] args) {
        System.out.print("Running " + FileCopyTest.class.getSimpleName() + " ... ");

        FileCopyTest test = new FileCopyTest();
        test.testCopyMissingFile();
        test.testCopy();

        System.out.println("OK");
    }

    private final @Mandatory FileCopy fileCopy = FileCopy.getInstance();
    private final @Mandatory FileReader fileReader = FileReader.getInstance();

    private void testCopyMissingFile() {
        TestProgress progress = new TestProgress();
        Assertions.assertThatCode(() -> fileCopy.copy(MISSING_PATH, COPY_PATH, progress))
                .withMessage("Expected missing file to cause file system exception.")
                .throwsException(FileSystemException.class);
        progress.verifySkip();
    }

    private void testCopy() {
        try {
            TestProgress progress = new TestProgress();
            File original = fileReader.read(SOURCE_PATH);
            fileCopy.copy(SOURCE_PATH, COPY_PATH, progress);
            File copy = fileReader.read(COPY_PATH);
            Assert.assertEquals(original.getProperties().getSize(), copy.getProperties().getSize());
            Assert.assertEquals(original.getProperties().getModified(), copy.getProperties().getModified());
            Assert.assertEquals(read(SOURCE_PATH), read(COPY_PATH));
            progress.verify(2L, 2L);

            Assertions.assertThatCode(() -> fileCopy.copy(SOURCE_PATH, COPY_PATH, progress))
                .withMessage("Should not be able to overwrite existing file.")
                .throwsException(FileSystemException.class);

            Assert.assertEquals(read(SOURCE_PATH), read(COPY_PATH));
        } finally {
            Assertions.assertThatCode(() -> Files.delete(COPY_PATH))
                .withMessage("Test cleanup failed.")
                .doesNotThrowAnyException();
        }
    }

    private @Mandatory String read(@Mandatory Path path) {
        return Assertions.assertThatFunction(() -> Files.readString(path))
            .withMessage("Could not read file '" + path + "'.")
            .doesNotThrowAnyException();
    }
}
