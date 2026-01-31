package cz.mg.backup.services;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.classes.Test;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.backup.entities.Directory;
import cz.mg.backup.entities.Node;
import cz.mg.backup.test.predicates.Equals;
import cz.mg.backup.test.TestFactory;
import cz.mg.backup.test.TestProgress;
import cz.mg.backup.test.predicates.Range;
import cz.mg.test.Assert;

public @Test class DirectorySortTest {
    public static void main(String[] args) {
        System.out.print("Running " + DirectorySortTest.class.getSimpleName() + " ... ");

        DirectorySortTest test = new DirectorySortTest();
        test.testEmpty();
        test.testSort();

        System.out.println("OK");
    }

    private final @Service DirectorySort sort = DirectorySort.getInstance();
    private final @Service TestFactory f = TestFactory.getInstance();

    private void testEmpty() {
        Directory directory = new Directory();

        TestProgress progress = new TestProgress();
        sort.sort(directory, progress);

        progress.verify(0L, 0L);
    }

    private void testSort() {
        Directory directory = f.directory(
            "foo",
            f.directory("B"),
            f.directory("AA"),
            f.directory("BB"),
            f.directory("A"),
            f.file("22"),
            f.file("11"),
            f.file("2"),
            f.file("1")
        );

        TestProgress progress = new TestProgress();
        sort.sort(directory, progress);

        verifyName("A", directory.getDirectories().get(0));
        verifyName("AA", directory.getDirectories().get(1));
        verifyName("B", directory.getDirectories().get(2));
        verifyName("BB", directory.getDirectories().get(3));
        verifyName("1", directory.getFiles().get(0));
        verifyName("11", directory.getFiles().get(1));
        verifyName("2", directory.getFiles().get(2));
        verifyName("22", directory.getFiles().get(3));
        progress.verify(new Equals(16L), new Range(4L, 16L));
    }

    private void verifyName(@Mandatory String expectation, @Mandatory Node node) {
        Assert.assertEquals(expectation, node.getPath().getFileName().toString());
    }
}
