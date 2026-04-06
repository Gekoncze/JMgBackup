package cz.mg.backup.services.matcher;

import cz.mg.annotations.requirement.Mandatory;
import cz.mg.backup.entities.Directory;
import cz.mg.backup.entities.File;
import cz.mg.backup.test.TestFactory;
import cz.mg.backup.test.TestProgress;
import cz.mg.collections.list.List;
import cz.mg.functions.EqualsFunctions;
import cz.mg.test.Assertions;

public class FileGrouperTest {
    public static void main(String[] args) {
        System.out.print("Running " + FileGrouperTest.class.getSimpleName() + " ... ");

        FileGrouperTest test = new FileGrouperTest();
        test.testGroupNull();
        test.testGroupNoFiles();
        test.testGroupOneFile();
        test.testGroupMultipleUniqueFiles();
        test.testGroupMultipleMatchingFiles();
        test.testGroupMultipleMixedFiles();

        System.out.println("OK");
    }

    private final @Mandatory FileGrouper fileGrouper = FileGrouper.getInstance();
    private final @Mandatory TestFactory f = TestFactory.getInstance();
    private final @Mandatory Converter converter = new Converter(true, true, true, true);

    private void testGroupNull() {
        TestProgress progress = new TestProgress();
        var result = fileGrouper.groupFiles(null, converter, progress);

        Assertions.assertThat(result.isEmpty())
            .withMessage("Empty map should be returned for null directory.")
            .isEqualTo(true);

        progress.verify(0L, 0L);
    }

    private void testGroupNoFiles() {
        Directory directory = f.directory("dir");

        TestProgress progress = new TestProgress();
        var result = fileGrouper.groupFiles(directory, converter, progress);

        Assertions.assertThat(result.isEmpty())
            .withMessage("Empty map should be returned for empty directory.")
            .isEqualTo(true);

        progress.verify(0L, 0L);
    }

    private void testGroupOneFile() {
        File file = f.file("foo");
        Directory directory = f.directory("dir", file);

        TestProgress progress = new TestProgress();
        var result = fileGrouper.groupFiles(directory, converter, progress);

        Assertions.assertThat(result.count())
            .withMessage("One group should be returned for single file.")
            .isEqualTo(1);

        Assertions.assertThatCollection(result.iterator().next().getValue())
                .withMessage("Found file should equal expected file.")
                .withEqualsFunction((EqualsFunctions.REFERENCE()))
                .isEqualTo(new List<>(file));

        progress.verify(1L, 1L);
    }

    private void testGroupMultipleUniqueFiles() {
        File one = f.file("one");
        File two = f.file("two");
        File three = f.file("three");
        Directory directory = f.directory("dir", one, two, three);

        TestProgress progress = new TestProgress();
        var result = fileGrouper.groupFiles(directory, converter, progress);

        Assertions.assertThat(result.count())
            .withMessage("Three groups should be returned for three unique files.")
            .isEqualTo(3);

        var iterator = result.iterator();

        Assertions.assertThatCollection(iterator.next().getValue())
            .withMessage("Found file should equal expected file.")
            .withEqualsFunction((EqualsFunctions.REFERENCE()))
            .isEqualTo(new List<>(one));

        Assertions.assertThatCollection(iterator.next().getValue())
            .withMessage("Found file should equal expected file.")
            .withEqualsFunction((EqualsFunctions.REFERENCE()))
            .isEqualTo(new List<>(two));

        Assertions.assertThatCollection(iterator.next().getValue())
            .withMessage("Found file should equal expected file.")
            .withEqualsFunction((EqualsFunctions.REFERENCE()))
            .isEqualTo(new List<>(three));

        progress.verify(3L, 3L);
    }

    private void testGroupMultipleMatchingFiles() {
        File one = f.file("foo");
        File two = f.file("foo");
        File three = f.file("foo");
        Directory directory = f.directory("dir",
            f.directory("1", one),
            f.directory("2", two),
            f.directory("3", three)
        );

        TestProgress progress = new TestProgress();
        var result = fileGrouper.groupFiles(directory, converter, progress);

        Assertions.assertThat(result.count())
            .withMessage("One group should be returned for three matching files.")
            .isEqualTo(1);

        Assertions.assertThatCollection(result.iterator().next().getValue())
            .withMessage("Found files should equal expected files.")
            .withEqualsFunction((EqualsFunctions.REFERENCE()))
            .isEqualTo(new List<>(one, two, three));
    }

    private void testGroupMultipleMixedFiles() {
        File one = f.file("foo");
        File two = f.file("bar");
        File three = f.file("foo");
        Directory directory = f.directory("dir",
            f.directory("1", one),
            f.directory("2", two),
            f.directory("3", three)
        );

        TestProgress progress = new TestProgress();
        var result = fileGrouper.groupFiles(directory, converter, progress);

        Assertions.assertThat(result.count())
            .withMessage("Two groups should be returned for given mixed files.")
            .isEqualTo(2);

        var iterator = result.iterator();

        Assertions.assertThatCollection(iterator.next().getValue())
            .withMessage("Found files should equal expected files.")
            .withEqualsFunction((EqualsFunctions.REFERENCE()))
            .isEqualTo(new List<>(one, three));

        Assertions.assertThatCollection(iterator.next().getValue())
            .withMessage("Found files should equal expected files.")
            .withEqualsFunction((EqualsFunctions.REFERENCE()))
            .isEqualTo(new List<>(two));
    }
}
