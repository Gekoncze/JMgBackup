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

import java.nio.file.Path;

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
            f.directory(Path.of("Y", "AA")),
            f.directory(Path.of("Z", "A")),
            f.directory(Path.of("W", "BB")),
            f.directory(Path.of("y", "B")),
            f.file(Path.of("W", "BB")),
            f.file(Path.of("Y", "AA")),
            f.file(Path.of("y", "B")),
            f.file(Path.of("Z", "A"))
        );

        TestProgress progress = new TestProgress();
        sort.sort(directory, progress);

        checkName("A", directory.getDirectories().get(0));
        checkName("AA", directory.getDirectories().get(1));
        checkName("B", directory.getDirectories().get(2));
        checkName("BB", directory.getDirectories().get(3));
        checkName("A", directory.getFiles().get(0));
        checkName("AA", directory.getFiles().get(1));
        checkName("B", directory.getFiles().get(2));
        checkName("BB", directory.getFiles().get(3));
        progress.verify(new Equals(16L), new Range(4L, 16L));
    }

    private void checkName(@Mandatory String expectation, @Mandatory Node node) {
        Assert.assertEquals(expectation, node.getPath().getFileName().toString());
    }
}
