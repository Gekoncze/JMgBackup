package cz.mg.backup.services;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.classes.Test;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.backup.components.Progress;
import cz.mg.backup.entities.Directory;
import cz.mg.backup.entities.File;
import cz.mg.backup.entities.Node;
import cz.mg.collections.list.List;
import cz.mg.test.Assert;
import cz.mg.test.exceptions.AssertException;

import java.nio.file.Path;

public @Test class TreeIteratorTest {
    public static void main(String[] args) {
        System.out.print("Running " + TreeIteratorTest.class.getSimpleName() + " ... ");

        TreeIteratorTest test = new TreeIteratorTest();
        test.testEmpty();
        test.testFile();
        test.testDirectory();
        test.testFileInDirectory();
        test.testNodesInDirectory();
        test.testNestedDirectories();
        test.testFiles();
        test.testDirectories();
        test.testEmptyCollection();

        System.out.println("OK");
    }

    private final @Service TreeIterator treeIterator = TreeIterator.getInstance();

    private void fail(Node node) {
        throw new AssertException("No consumer should be called.");
    }

    private void testEmpty() {
        Progress nodeProgress = new Progress();
        treeIterator.forEachNode((Node) null, this::fail, nodeProgress, "test");
        Assert.assertEquals(0L, nodeProgress.getLimit());
        Assert.assertEquals(0L, nodeProgress.getValue());

        Progress fileProgress = new Progress();
        treeIterator.forEachFile((Node) null, this::fail, fileProgress, "test");
        Assert.assertEquals(0L, fileProgress.getLimit());
        Assert.assertEquals(0L, fileProgress.getValue());

        Progress directoryProgress = new Progress();
        treeIterator.forEachDirectory((Node) null, this::fail, directoryProgress, "test");
        Assert.assertEquals(0L, directoryProgress.getLimit());
        Assert.assertEquals(0L, directoryProgress.getValue());
    }

    private void testFile() {
        File file = createFile(Path.of("f"));

        Progress nodeProgress = new Progress();
        treeIterator.forEachNode(file, this::add, nodeProgress, "test");

        Assert.assertEquals(Path.of("ff"), file.getPath());
        Assert.assertEquals(1L, nodeProgress.getLimit());
        Assert.assertEquals(1L, nodeProgress.getValue());

        Progress fileProgress = new Progress();
        treeIterator.forEachFile(file, this::add, fileProgress, "test");

        Assert.assertEquals(Path.of("ffff"), file.getPath());
        Assert.assertEquals(1L, fileProgress.getLimit());
        Assert.assertEquals(1L, fileProgress.getValue());

        Progress directoryProgress = new Progress();
        treeIterator.forEachDirectory(file, this::add, directoryProgress, "test");

        Assert.assertEquals(Path.of("ffff"), file.getPath());
        Assert.assertEquals(0L, directoryProgress.getLimit());
        Assert.assertEquals(0L, directoryProgress.getValue());
    }

    private void testDirectory() {
        Directory directory = createDirectory(Path.of("d"));

        Progress nodeProgress = new Progress();
        treeIterator.forEachNode(directory, this::add, nodeProgress, "test");

        Assert.assertEquals(Path.of("dd"), directory.getPath());
        Assert.assertEquals(1L, nodeProgress.getLimit());
        Assert.assertEquals(1L, nodeProgress.getValue());

        Progress fileProgress = new Progress();
        treeIterator.forEachFile(directory, this::add, fileProgress, "test");

        Assert.assertEquals(Path.of("dd"), directory.getPath());
        Assert.assertEquals(0L, fileProgress.getLimit());
        Assert.assertEquals(0L, fileProgress.getValue());

        Progress directoryProgress = new Progress();
        treeIterator.forEachDirectory(directory, this::add, directoryProgress, "test");

        Assert.assertEquals(Path.of("dddd"), directory.getPath());
        Assert.assertEquals(1L, directoryProgress.getLimit());
        Assert.assertEquals(1L, directoryProgress.getValue());
    }

    private void testFileInDirectory() {
        File file = createFile(Path.of("f"));
        Directory directory = createDirectory(Path.of("d"), file);
        directory.getProperties().setTotalFileCount(1);

        Progress nodeProgress = new Progress();
        treeIterator.forEachNode(directory, this::add, nodeProgress, "test");

        Assert.assertEquals(Path.of("ff"), file.getPath());
        Assert.assertEquals(Path.of("dd"), directory.getPath());
        Assert.assertEquals(2L, nodeProgress.getLimit());
        Assert.assertEquals(2L, nodeProgress.getValue());

        Progress fileProgress = new Progress();
        treeIterator.forEachFile(directory, this::add, fileProgress, "test");

        Assert.assertEquals(Path.of("ffff"), file.getPath());
        Assert.assertEquals(Path.of("dd"), directory.getPath());
        Assert.assertEquals(1L, fileProgress.getLimit());
        Assert.assertEquals(1L, fileProgress.getValue());

        Progress directoryProgress = new Progress();
        treeIterator.forEachDirectory(directory, this::add, directoryProgress, "test");

        Assert.assertEquals(Path.of("ffff"), file.getPath());
        Assert.assertEquals(Path.of("dddd"), directory.getPath());
        Assert.assertEquals(1L, directoryProgress.getLimit());
        Assert.assertEquals(1L, directoryProgress.getValue());
    }

    private void testNodesInDirectory() {
        File file = createFile(Path.of("f1"));
        Directory directory = createDirectory(Path.of("d1"), file);
        File secondFile = createFile(Path.of("f2"));
        Directory secondDirectory = createDirectory(Path.of("d2"), secondFile);
        directory.getDirectories().addLast(secondDirectory);
        directory.getProperties().setTotalFileCount(2);
        directory.getProperties().setTotalDirectoryCount(1);

        Progress nodeProgress = new Progress();
        treeIterator.forEachNode(directory, this::add, nodeProgress, "test");

        Assert.assertEquals(Path.of("f1f1"), file.getPath());
        Assert.assertEquals(Path.of("d1d1"), directory.getPath());
        Assert.assertEquals(Path.of("f2f2"), secondFile.getPath());
        Assert.assertEquals(Path.of("d2d2"), secondDirectory.getPath());
        Assert.assertEquals(4L, nodeProgress.getLimit());
        Assert.assertEquals(4L, nodeProgress.getValue());

        Progress fileProgress = new Progress();
        treeIterator.forEachFile(directory, this::add, fileProgress, "test");

        Assert.assertEquals(Path.of("f1f1f1f1"), file.getPath());
        Assert.assertEquals(Path.of("d1d1"), directory.getPath());
        Assert.assertEquals(Path.of("f2f2f2f2"), secondFile.getPath());
        Assert.assertEquals(Path.of("d2d2"), secondDirectory.getPath());
        Assert.assertEquals(2L, fileProgress.getLimit());
        Assert.assertEquals(2L, fileProgress.getValue());

        Progress directoryProgress = new Progress();
        treeIterator.forEachDirectory(directory, this::add, directoryProgress, "test");

        Assert.assertEquals(Path.of("f1f1f1f1"), file.getPath());
        Assert.assertEquals(Path.of("d1d1d1d1"), directory.getPath());
        Assert.assertEquals(Path.of("f2f2f2f2"), secondFile.getPath());
        Assert.assertEquals(Path.of("d2d2d2d2"), secondDirectory.getPath());
        Assert.assertEquals(2L, directoryProgress.getLimit());
        Assert.assertEquals(2L, directoryProgress.getValue());
    }

    private void testNestedDirectories() {
        Directory directory1 = createDirectory(Path.of("d1"));
        Directory directory2 = createDirectory(Path.of("d2"));
        Directory directory3 = createDirectory(Path.of("d3"));
        directory1.getDirectories().addLast(directory2);
        directory2.getDirectories().addLast(directory3);
        directory1.getProperties().setTotalDirectoryCount(2);

        Progress nodeProgress = new Progress();
        treeIterator.forEachNode(directory1, this::add, nodeProgress, "test");

        Assert.assertEquals(Path.of("d1d1"), directory1.getPath());
        Assert.assertEquals(Path.of("d2d2"), directory2.getPath());
        Assert.assertEquals(Path.of("d3d3"), directory3.getPath());
        Assert.assertEquals(3L, nodeProgress.getLimit());
        Assert.assertEquals(3L, nodeProgress.getValue());

        Progress fileProgress = new Progress();
        treeIterator.forEachFile(directory1, this::add, fileProgress, "test");

        Assert.assertEquals(Path.of("d1d1"), directory1.getPath());
        Assert.assertEquals(Path.of("d2d2"), directory2.getPath());
        Assert.assertEquals(Path.of("d3d3"), directory3.getPath());
        Assert.assertEquals(0L, fileProgress.getLimit());
        Assert.assertEquals(0L, fileProgress.getValue());

        Progress directoryProgress = new Progress();
        treeIterator.forEachDirectory(directory1, this::add, directoryProgress, "test");

        Assert.assertEquals(Path.of("d1d1d1d1"), directory1.getPath());
        Assert.assertEquals(Path.of("d2d2d2d2"), directory2.getPath());
        Assert.assertEquals(Path.of("d3d3d3d3"), directory3.getPath());
        Assert.assertEquals(3L, directoryProgress.getLimit());
        Assert.assertEquals(3L, directoryProgress.getValue());
    }

    private void testFiles() {
        File file1 = createFile(Path.of("f1"));
        File file2 = createFile(Path.of("f2"));
        File file3 = createFile(Path.of("f3"));
        List<File> files = new List<>(file1, file2, file3);

        Progress nodeProgress = new Progress();
        treeIterator.forEachNode(files, this::add, nodeProgress, "test");

        Assert.assertEquals(Path.of("f1f1"), file1.getPath());
        Assert.assertEquals(Path.of("f2f2"), file2.getPath());
        Assert.assertEquals(Path.of("f3f3"), file3.getPath());
        Assert.assertEquals(3L, nodeProgress.getLimit());
        Assert.assertEquals(3L, nodeProgress.getValue());

        Progress fileProgress = new Progress();
        treeIterator.forEachFile(files, this::add, fileProgress, "test");

        Assert.assertEquals(Path.of("f1f1f1f1"), file1.getPath());
        Assert.assertEquals(Path.of("f2f2f2f2"), file2.getPath());
        Assert.assertEquals(Path.of("f3f3f3f3"), file3.getPath());
        Assert.assertEquals(3L, fileProgress.getLimit());
        Assert.assertEquals(3L, fileProgress.getValue());

        Progress directoryProgress = new Progress();
        treeIterator.forEachDirectory(files, this::add, directoryProgress, "test");

        Assert.assertEquals(Path.of("f1f1f1f1"), file1.getPath());
        Assert.assertEquals(Path.of("f2f2f2f2"), file2.getPath());
        Assert.assertEquals(Path.of("f3f3f3f3"), file3.getPath());
        Assert.assertEquals(0L, directoryProgress.getLimit());
        Assert.assertEquals(0L, directoryProgress.getValue());
    }

    private void testDirectories() {
        Directory directory1 = createDirectory(Path.of("d1"));
        Directory directory2 = createDirectory(Path.of("d2"));
        Directory directory3 = createDirectory(Path.of("d3"));
        List<Directory> directories = new List<>(directory1, directory2, directory3);

        Progress nodeProgress = new Progress();
        treeIterator.forEachNode(directories, this::add, nodeProgress, "test");

        Assert.assertEquals(Path.of("d1d1"), directory1.getPath());
        Assert.assertEquals(Path.of("d2d2"), directory2.getPath());
        Assert.assertEquals(Path.of("d3d3"), directory3.getPath());
        Assert.assertEquals(3L, nodeProgress.getLimit());
        Assert.assertEquals(3L, nodeProgress.getValue());

        Progress fileProgress = new Progress();
        treeIterator.forEachFile(directories, this::add, fileProgress, "test");

        Assert.assertEquals(Path.of("d1d1"), directory1.getPath());
        Assert.assertEquals(Path.of("d2d2"), directory2.getPath());
        Assert.assertEquals(Path.of("d3d3"), directory3.getPath());
        Assert.assertEquals(0L, fileProgress.getLimit());
        Assert.assertEquals(0L, fileProgress.getValue());

        Progress directoryProgress = new Progress();
        treeIterator.forEachDirectory(directories, this::add, directoryProgress, "test");

        Assert.assertEquals(Path.of("d1d1d1d1"), directory1.getPath());
        Assert.assertEquals(Path.of("d2d2d2d2"), directory2.getPath());
        Assert.assertEquals(Path.of("d3d3d3d3"), directory3.getPath());
        Assert.assertEquals(3L, directoryProgress.getLimit());
        Assert.assertEquals(3L, directoryProgress.getValue());
    }

    private void testEmptyCollection() {
        List<Node> directories = new List<>();

        Progress nodeProgress = new Progress();
        treeIterator.forEachNode(directories, this::fail, nodeProgress, "test");

        Assert.assertEquals(0L, nodeProgress.getLimit());
        Assert.assertEquals(0L, nodeProgress.getValue());

        Progress fileProgress = new Progress();
        treeIterator.forEachFile(directories, this::fail, fileProgress, "test");

        Assert.assertEquals(0L, fileProgress.getLimit());
        Assert.assertEquals(0L, fileProgress.getValue());

        Progress directoryProgress = new Progress();
        treeIterator.forEachDirectory(directories, this::fail, directoryProgress, "test");

        Assert.assertEquals(0L, directoryProgress.getLimit());
        Assert.assertEquals(0L, directoryProgress.getValue());
    }

    private void add(@Mandatory Node n) {
        n.setPath(Path.of(n.getPath().toString() + n.getPath().toString()));
    }

    private @Mandatory Directory createDirectory(@Mandatory Path path) {
        Directory directory = new Directory();
        directory.setPath(path);
        return directory;
    }

    private @Mandatory Directory createDirectory(@Mandatory Path path, @Mandatory File file) {
        Directory directory = new Directory();
        directory.setPath(path);
        directory.getFiles().addLast(file);
        return directory;
    }

    private @Mandatory File createFile(@Mandatory Path path) {
        File file = new File();
        file.setPath(path);
        return file;
    }
}
