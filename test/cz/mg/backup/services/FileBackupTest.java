package cz.mg.backup.services;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.classes.Test;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.backup.Configuration;
import cz.mg.backup.components.Progress;
import cz.mg.backup.entities.Algorithm;
import cz.mg.backup.entities.Directory;
import cz.mg.backup.entities.File;
import cz.mg.backup.exceptions.MissingException;
import cz.mg.backup.services.comparator.DirectoryComparator;
import cz.mg.backup.test.TestFactory;
import cz.mg.backup.test.TestProgress;
import cz.mg.collections.list.List;
import cz.mg.test.Assert;
import cz.mg.test.Assertions;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public @Test class FileBackupTest {
    private static final @Mandatory Path PARENT_DIRECTORY = Configuration.getRoot(FileBackupTest.class);
    private static final @Mandatory Path SOURCE_DIRECTORY = PARENT_DIRECTORY.resolve("source");
    private static final @Mandatory Path SOURCE_EXISTING_FILE = SOURCE_DIRECTORY.resolve("existing.txt");
    private static final @Mandatory Path SOURCE_MISSING_FILE = SOURCE_DIRECTORY.resolve("missing.txt");
    private static final @Mandatory Path TARGET_DIRECTORY = PARENT_DIRECTORY.resolve("target");
    private static final @Mandatory Path TARGET_EXISTING_FILE = TARGET_DIRECTORY.resolve("existing.txt");
    private static final @Mandatory Path TARGET_MISSING_FILE = TARGET_DIRECTORY.resolve("missing.txt");

    public static void main(String[] args) {
        System.out.print("Running " + FileBackupTest.class.getSimpleName() + " ... ");

        FileBackupTest test = new FileBackupTest();
        test.testCopy();
        test.testValidateSameDirectories();

        System.out.println("OK");
    }

    private final @Service FileBackup fileBackup = FileBackup.getInstance();
    private final @Service DirectoryReader reader = DirectoryReader.getInstance();
    private final @Service DirectoryComparator comparator = DirectoryComparator.getInstance();
    private final @Service TestFactory f = TestFactory.getInstance();

    private void testCopy() {
        Assert.assertEquals(true, Files.exists(SOURCE_DIRECTORY));
        Assert.assertEquals(true, Files.exists(SOURCE_EXISTING_FILE));
        Assert.assertEquals(true, Files.exists(SOURCE_MISSING_FILE));
        Assert.assertEquals(true, Files.exists(TARGET_DIRECTORY));
        Assert.assertEquals(true, Files.exists(TARGET_EXISTING_FILE));
        Assert.assertEquals(false, Files.exists(TARGET_MISSING_FILE));
        Assert.assertNotEquals(SOURCE_DIRECTORY, TARGET_DIRECTORY);
        Assert.assertNotEquals(getSize(SOURCE_EXISTING_FILE), getSize(TARGET_EXISTING_FILE));

        long originalTargetExistingFileSize = getSize(TARGET_EXISTING_FILE);

        Directory sourceDirectory = reader.read(SOURCE_DIRECTORY, new Progress());
        Directory targetDirectory = reader.read(TARGET_DIRECTORY, new Progress());
        comparator.compare(sourceDirectory, targetDirectory, new Progress());

        try {
            TestProgress progress = new TestProgress();
            fileBackup.copyMissingFiles(
                new List<>(sourceDirectory),
                sourceDirectory,
                targetDirectory,
                Algorithm.MD5,
                progress
            );

            Assert.assertEquals(true, Files.exists(TARGET_MISSING_FILE));
            Assert.assertEquals(originalTargetExistingFileSize, getSize(TARGET_EXISTING_FILE));
            Assert.assertEquals(2, targetDirectory.getProperties().getTotalFileCount());
            progress.verify(2, 2);
        } finally {
            if (Files.exists(TARGET_MISSING_FILE)) {
                Assertions.assertThatCode(() -> Files.delete(TARGET_MISSING_FILE))
                    .withMessage("Test cleanup failed!")
                    .doesNotThrowAnyException();
            }
        }
    }

    private void testValidateSameDirectories() {
        Directory source = new Directory();
        source.setPath(SOURCE_DIRECTORY);

        Directory target = new Directory();
        target.setPath(SOURCE_DIRECTORY);

        TestProgress progress = new TestProgress();
        Assertions.assertThatCode(() -> fileBackup.copyMissingFiles(new List<>(), source, target, Algorithm.MD5, progress))
            .withMessage("Source and target directory should not be allowed to be the same.")
            .throwsException(IllegalArgumentException.class);

        progress.verifySkip();
    }

    private long getSize(@Mandatory Path path) {
        try {
            return Files.size(path);
        } catch (IOException e) {
            throw new IllegalStateException("Could not get size of file '" + path + "'.");
        }
    }
}
