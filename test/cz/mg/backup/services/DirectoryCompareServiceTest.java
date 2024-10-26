package cz.mg.backup.services;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.classes.Test;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.annotations.requirement.Optional;
import cz.mg.backup.entities.Directory;
import cz.mg.backup.entities.File;
import cz.mg.backup.entities.Properties;
import cz.mg.backup.exceptions.CompareException;
import cz.mg.collections.list.List;
import cz.mg.test.Assert;
import cz.mg.test.exceptions.AssertException;

import java.nio.file.Path;

public @Test class DirectoryCompareServiceTest {
    public static void main(String[] args) {
        System.out.print("Running " + DirectoryCompareServiceTest.class.getSimpleName() + " ... ");

        DirectoryCompareServiceTest test = new DirectoryCompareServiceTest();
        test.testEqual();
        test.testMissingDirectory();
        test.testMissingFile();
        test.testDifferentFile();
        test.testRecursive();
        test.testCompareClearsCompareErrors();
        test.testClearCompareErrors();

        System.out.println("OK");
    }

    private final @Service DirectoryCompareService service = DirectoryCompareService.getInstance();

    private void testEqual() {
        testEqual(createDirectory("foo"), createDirectory("foo"));
        testEqual(
            createDirectory("foobar", new List<>(createDirectory("foo")), new List<>(createFile("bar"))),
            createDirectory("foobar", new List<>(createDirectory("foo")), new List<>(createFile("bar")))
        );
        testEqual(
            createDirectory(
                "root",
                new List<>(
                    createDirectory(
                        "foobar",
                        new List<>(),
                        new List<>(createFile("foo"), createFile("bar", 1L, "11"))
                    )
                ), new List<>()
            ),
            createDirectory(
                "root",
                new List<>(
                    createDirectory(
                        "foobar",
                        new List<>(),
                        new List<>(createFile("foo"), createFile("bar", 1L, "11"))
                    )
                ), new List<>()
            )
        );
    }

    private void testEqual(@Mandatory Directory first, @Mandatory Directory second) {
        service.compare(first, second);
        assertNoErrors(first);
        assertNoErrors(second);
    }

    private void assertNoErrors(@Mandatory Directory directory) {
        if (!directory.getErrors().isEmpty()) {
            throw new AssertException("Unexpected error.", directory.getErrors().getFirst());
        }

        for (Directory child : directory.getDirectories()) {
            assertNoErrors(child);
        }

        for (File child : directory.getFiles()) {
            assertNoErrors(child);
        }
    }

    private void assertNoErrors(@Mandatory File file) {
        if (!file.getErrors().isEmpty()) {
            throw new AssertException("Unexpected error.", file.getErrors().getFirst());
        }
    }

    private void testMissingDirectory() {
        testMissingFirstDirectory();
        testMissingSecondDirectory();
        testMissingBothDirectories();
    }

    private void testMissingFirstDirectory() {
        Directory firstL1 = createDirectory("L1", new List<>(), new List<>());
        Directory secondL2 = createDirectory("L2");
        Directory secondL1 = createDirectory("L1", new List<>(secondL2), new List<>());
        service.compare(firstL1, secondL1);
        Assert.assertEquals(0, firstL1.getErrors().count());
        Assert.assertEquals(1, secondL2.getErrors().count());
        Assert.assertEquals(0, secondL1.getErrors().count());
    }

    private void testMissingSecondDirectory() {
        Directory firstL2 = createDirectory("L2");
        Directory firstL1 = createDirectory("L1", new List<>(firstL2), new List<>());
        Directory secondL1 = createDirectory("L1", new List<>(), new List<>());
        service.compare(firstL1, secondL1);
        Assert.assertEquals(1, firstL2.getErrors().count());
        Assert.assertEquals(0, firstL1.getErrors().count());
        Assert.assertEquals(0, secondL1.getErrors().count());
    }

    private void testMissingBothDirectories() {
        Directory firstL2 = createDirectory("L2 first");
        Directory firstL1 = createDirectory("L1", new List<>(firstL2), new List<>());
        Directory secondL2 = createDirectory("L2 second");
        Directory secondL1 = createDirectory("L1", new List<>(secondL2), new List<>());
        service.compare(firstL1, secondL1);
        Assert.assertEquals(1, firstL2.getErrors().count());
        Assert.assertEquals(0, firstL1.getErrors().count());
        Assert.assertEquals(1, secondL2.getErrors().count());
        Assert.assertEquals(0, secondL1.getErrors().count());
    }

    private void testMissingFile() {
        testMissingFirstFile();
        testMissingSecondFile();
        testMissingBothFiles();
    }

    private void testMissingFirstFile() {
        Directory firstL1 = createDirectory("L1", new List<>(), new List<>());
        File secondL2 = createFile("L2");
        Directory secondL1 = createDirectory("L1", new List<>(), new List<>(secondL2));
        service.compare(firstL1, secondL1);
        Assert.assertEquals(0, firstL1.getErrors().count());
        Assert.assertEquals(1, secondL2.getErrors().count());
        Assert.assertEquals(0, secondL1.getErrors().count());
    }

    private void testMissingSecondFile() {
        File firstL2 = createFile("L2");
        Directory firstL1 = createDirectory("L1", new List<>(), new List<>(firstL2));
        Directory secondL1 = createDirectory("L1", new List<>(), new List<>());
        service.compare(firstL1, secondL1);
        Assert.assertEquals(1, firstL2.getErrors().count());
        Assert.assertEquals(0, firstL1.getErrors().count());
        Assert.assertEquals(0, secondL1.getErrors().count());
    }

    private void testMissingBothFiles() {
        File firstL2 = createFile("L2 first");
        Directory firstL1 = createDirectory("L1", new List<>(), new List<>(firstL2));
        File secondL2 = createFile("L2 second");
        Directory secondL1 = createDirectory("L1", new List<>(), new List<>(secondL2));
        service.compare(firstL1, secondL1);
        Assert.assertEquals(1, firstL2.getErrors().count());
        Assert.assertEquals(0, firstL1.getErrors().count());
        Assert.assertEquals(1, secondL2.getErrors().count());
        Assert.assertEquals(0, secondL1.getErrors().count());
    }

    private void testDifferentFile() {
        File firstFile = createFile("foo", 1L, null);
        File secondFile = createFile("foo", 2L, null);
        Directory firstDirectory = createDirectory("root", new List<>(), new List<>(firstFile));
        Directory secondDirectory = createDirectory("root", new List<>(), new List<>(secondFile));
        service.compare(firstDirectory, secondDirectory);
        Assert.assertEquals(true, firstDirectory.getErrors().isEmpty());
        Assert.assertEquals(false, firstFile.getErrors().isEmpty());
        Assert.assertEquals(true, secondDirectory.getErrors().isEmpty());
        Assert.assertEquals(false, secondFile.getErrors().isEmpty());
    }

    private void testRecursive() {
        Directory firstL3 = createDirectory("L3 first");

        Directory firstL2 = createDirectory(
            "L2",
            new List<>(firstL3),
            new List<>()
        );

        Directory firstL2a = createDirectory("L2a");
        Directory firstL2b = createDirectory("L2b");

        Directory firstL1 = createDirectory(
            "L1",
            new List<>(firstL2a, firstL2, firstL2b),
            new List<>()
        );

        Directory secondL3 = createDirectory("L3 second");

        Directory secondL2 = createDirectory(
            "L2",
            new List<>(secondL3),
            new List<>()
        );

        Directory secondL2a = createDirectory("L2a");
        Directory secondL2b = createDirectory("L2b");

        Directory secondL1 = createDirectory(
            "L1",
            new List<>(secondL2a, secondL2, secondL2b),
            new List<>()
        );

        service.compare(firstL1, secondL1);

        Assert.assertEquals(true, firstL1.getErrors().isEmpty());
        Assert.assertEquals(true, firstL2a.getErrors().isEmpty());
        Assert.assertEquals(true, firstL2.getErrors().isEmpty());
        Assert.assertEquals(true, firstL2b.getErrors().isEmpty());
        Assert.assertEquals(false, firstL3.getErrors().isEmpty());

        Assert.assertEquals(true, secondL1.getErrors().isEmpty());
        Assert.assertEquals(true, secondL2a.getErrors().isEmpty());
        Assert.assertEquals(true, secondL2.getErrors().isEmpty());
        Assert.assertEquals(true, secondL2b.getErrors().isEmpty());
        Assert.assertEquals(false, secondL3.getErrors().isEmpty());
    }

    private void testCompareClearsCompareErrors() {
        Directory first = createDirectory("root");
        Directory second = createDirectory("root");
        Assert.assertEquals(0, first.getErrors().count());
        Assert.assertEquals(0, second.getErrors().count());
        first.getErrors().addLast(new RuntimeException());
        second.getErrors().addLast(new IllegalArgumentException());
        first.getErrors().addLast(new CompareException("test"));
        second.getErrors().addLast(new CompareException("test"));
        Assert.assertEquals(2, first.getErrors().count());
        Assert.assertEquals(2, second.getErrors().count());
        service.compare(first, second);
        Assert.assertEquals(1, first.getErrors().count());
        Assert.assertEquals(1, second.getErrors().count());
        Assert.assertEquals(RuntimeException.class, first.getErrors().getFirst().getClass());
        Assert.assertEquals(IllegalArgumentException.class, second.getErrors().getFirst().getClass());
    }

    private void testClearCompareErrors() {
        Directory subdirectory = createDirectory("subdirectory");
        subdirectory.getErrors().addLast(new CompareException("0"));

        Directory directory = createDirectory("directory", new List<>(subdirectory), new List<>());
        directory.getErrors().addLast(new RuntimeException());
        directory.getErrors().addLast(new CompareException("1"));
        directory.getErrors().addLast(new CompareException("2"));
        directory.getErrors().addLast(new IllegalArgumentException());

        Assert.assertEquals(1, subdirectory.getErrors().count());
        Assert.assertEquals(4, directory.getErrors().count());

        service.compare(directory, null);

        Assert.assertEquals(0, subdirectory.getErrors().count());
        Assert.assertEquals(2, directory.getErrors().count());
        Assert.assertEquals(RuntimeException.class, directory.getErrors().getFirst().getClass());
        Assert.assertEquals(IllegalArgumentException.class, directory.getErrors().getLast().getClass());

        subdirectory.getErrors().addLast(new CompareException("0"));
        directory.getErrors().addLast(new CompareException("1"));
        directory.getErrors().addLast(new CompareException("2"));

        Assert.assertEquals(1, subdirectory.getErrors().count());
        Assert.assertEquals(4, directory.getErrors().count());

        service.compare(null, directory);

        Assert.assertEquals(0, subdirectory.getErrors().count());
        Assert.assertEquals(2, directory.getErrors().count());
        Assert.assertEquals(RuntimeException.class, directory.getErrors().getFirst().getClass());
        Assert.assertEquals(IllegalArgumentException.class, directory.getErrors().getLast().getClass());
    }

    private @Mandatory Directory createDirectory(@Mandatory String name) {
        return createDirectory(name, new List<>(), new List<>());
    }

    private @Mandatory Directory createDirectory(
        @Mandatory String name,
        @Mandatory List<Directory> directories,
        @Mandatory List<File> files
    ) {
        Directory directory = new Directory();
        directory.setPath(Path.of(name));
        directory.setDirectories(directories);
        directory.setFiles(files);
        return directory;
    }

    private @Mandatory File createFile(@Mandatory String name) {
        return createFile(name, null, null);
    }

    private @Mandatory File createFile(
        @Mandatory String name,
        @Optional Long size,
        @Optional String hash
    ) {
        File file = new File();
        file.setPath(Path.of(name));
        file.setProperties(new Properties());
        file.getProperties().setSize(size);
        file.getProperties().setHash(hash);
        return file;
    }
}
