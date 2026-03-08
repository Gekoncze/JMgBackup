package cz.mg.backup.services;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.classes.Test;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.backup.Configuration;
import cz.mg.backup.components.Progress;
import cz.mg.backup.entities.Algorithm;
import cz.mg.backup.entities.Directory;
import cz.mg.backup.entities.File;
import cz.mg.backup.services.comparator.DirectoryComparator;
import cz.mg.backup.test.TestProgress;
import cz.mg.collections.list.List;
import cz.mg.test.Assert;
import cz.mg.test.Assertions;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public @Test class BackupServiceTest {
    private static final @Mandatory Path PARENT_DIRECTORY = Configuration.getRoot(BackupServiceTest.class);
    private static final @Mandatory Path F_SOURCE_DIRECTORY = PARENT_DIRECTORY.resolve("file").resolve("source");
    private static final @Mandatory Path F_SOURCE_EXISTING_FILE = F_SOURCE_DIRECTORY.resolve("existing.txt");
    private static final @Mandatory Path F_SOURCE_MISSING_FILE = F_SOURCE_DIRECTORY.resolve("missing.txt");
    private static final @Mandatory Path F_TARGET_DIRECTORY = PARENT_DIRECTORY.resolve("file").resolve("target");
    private static final @Mandatory Path F_TARGET_EXISTING_FILE = F_TARGET_DIRECTORY.resolve("existing.txt");
    private static final @Mandatory Path F_TARGET_MISSING_FILE = F_TARGET_DIRECTORY.resolve("missing.txt");
    private static final @Mandatory Path D_SOURCE_DIRECTORY = PARENT_DIRECTORY.resolve("directory").resolve("source");
    private static final @Mandatory Path D_SOURCE_MISSING_DIRECTORY = D_SOURCE_DIRECTORY.resolve("missing");
    private static final @Mandatory Path D_TARGET_DIRECTORY = PARENT_DIRECTORY.resolve("directory").resolve("target");
    private static final @Mandatory Path D_TARGET_MISSING_DIRECTORY = D_TARGET_DIRECTORY.resolve("missing");

    public static void main(String[] args) {
        System.out.print("Running " + BackupServiceTest.class.getSimpleName() + " ... ");

        BackupServiceTest test = new BackupServiceTest();
        test.testCopyMissingFile();
        test.testCopyMissingFileSameSourceAndTarget();
        test.testCopyMissingFileNotInSource();
        test.testCreateMissingDirectory();
        test.testCreateMissingDirectorySameSourceAndTarget();
        test.testCreateMissingDirectoryNotInSource();

        System.out.println("OK");
    }

    private final @Service BackupService backupService = BackupService.getInstance();
    private final @Service DirectoryReader directoryReader = DirectoryReader.getInstance();
    private final @Service DirectoryComparator comparator = DirectoryComparator.getInstance();

    private void testCopyMissingFile() {
        Assert.assertEquals(true, Files.exists(F_SOURCE_DIRECTORY));
        Assert.assertEquals(true, Files.exists(F_SOURCE_EXISTING_FILE));
        Assert.assertEquals(true, Files.exists(F_SOURCE_MISSING_FILE));
        Assert.assertEquals(true, Files.exists(F_TARGET_DIRECTORY));
        Assert.assertEquals(true, Files.exists(F_TARGET_EXISTING_FILE));
        Assert.assertEquals(false, Files.exists(F_TARGET_MISSING_FILE));
        Assert.assertNotEquals(F_SOURCE_DIRECTORY, F_TARGET_DIRECTORY);
        Assert.assertNotEquals(getSize(F_SOURCE_EXISTING_FILE), getSize(F_TARGET_EXISTING_FILE));

        long originalTargetExistingFileSize = getSize(F_TARGET_EXISTING_FILE);

        Directory source = directoryReader.read(F_SOURCE_DIRECTORY, new Progress());
        Directory target = directoryReader.read(F_TARGET_DIRECTORY, new Progress());
        comparator.compare(source, target, new Progress());

        try {
            TestProgress progress = new TestProgress();
            List<File> files = backupService.collectMissingFiles(new List<>(source), progress);

            Assert.assertEquals(1, files.count());
            progress.verify(2, 2);

            progress = new TestProgress();
            backupService.copyMissingFiles(
                files,
                source,
                target,
                Algorithm.MD5,
                progress
            );

            Assert.assertEquals(true, Files.exists(F_TARGET_MISSING_FILE));
            Assert.assertEquals(originalTargetExistingFileSize, getSize(F_TARGET_EXISTING_FILE));
            Assert.assertEquals(2, target.getProperties().getTotalFileCount());
            Assert.assertEquals(F_TARGET_EXISTING_FILE, target.getFiles().get(0).getPath());
            Assert.assertEquals(F_TARGET_MISSING_FILE, target.getFiles().get(1).getPath());
            progress.verify(1, 1);
        } finally {
            if (Files.exists(F_TARGET_MISSING_FILE)) {
                Assertions.assertThatCode(() -> Files.delete(F_TARGET_MISSING_FILE))
                    .withMessage("Test cleanup failed!")
                    .doesNotThrowAnyException();
            }
        }
    }

    private void testCopyMissingFileSameSourceAndTarget() {
        Directory source = new Directory();
        source.setPath(F_SOURCE_DIRECTORY);

        Directory target = new Directory();
        target.setPath(F_SOURCE_DIRECTORY);

        TestProgress progress = new TestProgress();
        Assertions.assertThatCode(() -> backupService.copyMissingFiles(new List<>(), source, target, Algorithm.MD5, progress))
            .withMessage("Source and target directory should not be allowed to be the same.")
            .throwsException(IllegalArgumentException.class);

        progress.verifySkip();
    }

    private void testCopyMissingFileNotInSource() {
        Directory source = directoryReader.read(F_SOURCE_DIRECTORY, new Progress());
        Directory target = directoryReader.read(F_TARGET_DIRECTORY, new Progress());
        List<File> files = new List<>(target.getFiles().get(0));

        TestProgress progress = new TestProgress();
        Assertions.assertThatCode(() -> backupService.copyMissingFiles(files, source, target, Algorithm.MD5, progress))
            .withMessage("File not in source directory should not be allowed.")
            .throwsException(IllegalArgumentException.class);

        progress.verify();
    }

    private void testCreateMissingDirectory() {
        Assert.assertEquals(true, Files.exists(D_SOURCE_DIRECTORY));
        Assert.assertEquals(true, Files.exists(D_SOURCE_MISSING_DIRECTORY));
        Assert.assertEquals(true, Files.exists(D_TARGET_DIRECTORY));
        Assert.assertEquals(false, Files.exists(D_TARGET_MISSING_DIRECTORY));
        Assert.assertNotEquals(D_SOURCE_DIRECTORY, D_TARGET_DIRECTORY);

        Directory source = directoryReader.read(D_SOURCE_DIRECTORY, new Progress());
        Directory target = directoryReader.read(D_TARGET_DIRECTORY, new Progress());
        comparator.compare(source, target, new Progress());

        try {
            TestProgress progress = new TestProgress();
            List<Directory> directories = backupService.collectMissingDirectories(new List<>(source), progress);

            Assert.assertEquals(1, directories.count());
            progress.verify(2, 2);

            backupService.createMissingDirectories(directories, source, target, progress);

            Assert.assertEquals(true, Files.exists(D_TARGET_MISSING_DIRECTORY));
            Assert.assertEquals(2, target.getProperties().getTotalDirectoryCount());
            Assert.assertEquals(D_TARGET_MISSING_DIRECTORY, target.getDirectories().get(1).getPath());
            progress.verify(1, 1);
        } finally {
            if (Files.exists(D_TARGET_MISSING_DIRECTORY)) {
                Assertions.assertThatCode(() -> Files.delete(D_TARGET_MISSING_DIRECTORY))
                    .withMessage("Test cleanup failed.")
                    .doesNotThrowAnyException();
            }
        }
    }

    private void testCreateMissingDirectorySameSourceAndTarget() {
        Directory source = new Directory();
        source.setPath(D_SOURCE_DIRECTORY);

        Directory target = new Directory();
        target.setPath(D_SOURCE_DIRECTORY);

        TestProgress progress = new TestProgress();
        Assertions.assertThatCode(() -> backupService.createMissingDirectories(new List<>(), source, target, progress))
            .withMessage("Source and target directory should not be allowed to be the same.")
            .throwsException(IllegalArgumentException.class);

        progress.verifySkip();
    }

    private void testCreateMissingDirectoryNotInSource() {
        Directory source = directoryReader.read(D_SOURCE_DIRECTORY, new Progress());
        Directory target = directoryReader.read(D_TARGET_DIRECTORY, new Progress());
        List<Directory> directories = new List<>(target.getDirectories().get(0));

        TestProgress progress = new TestProgress();
        Assertions.assertThatCode(() -> backupService.createMissingDirectories(directories, source, target, progress))
            .withMessage("Directory not in source directory should not be allowed.")
            .throwsException(IllegalArgumentException.class);

        progress.verify();
    }

    private long getSize(@Mandatory Path path) {
        try {
            return Files.size(path);
        } catch (IOException e) {
            throw new IllegalStateException("Could not get size of file '" + path + "'.");
        }
    }
}
