package cz.mg.backup.services.matcher;

import cz.mg.annotations.requirement.Mandatory;
import cz.mg.backup.entities.Algorithm;
import cz.mg.backup.entities.File;
import cz.mg.backup.exceptions.DuplicateException;
import cz.mg.backup.test.TestFactory;
import cz.mg.backup.test.TestProgress;
import cz.mg.collections.list.List;
import cz.mg.collections.map.Map;
import cz.mg.collections.pair.Pair;
import cz.mg.functions.EqualsFunctions;
import cz.mg.test.Assert;
import cz.mg.test.Assertions;

public class DuplicateDetectorTest {
    public static void main(String[] args) {
        System.out.print("Running " + DuplicateDetectorTest.class.getSimpleName() + " ... ");

        DuplicateDetectorTest test = new DuplicateDetectorTest();
        test.testEmpty();
        test.testOneFile();
        test.testUniqueFiles();
        test.testMatchingFiles();
        test.testMixedFiles();
        test.testExistingError();

        System.out.println("OK");
    }

    private final @Mandatory DuplicateDetector detector = DuplicateDetector.getInstance();
    private final @Mandatory TestFactory f = TestFactory.getInstance();

    private void testEmpty() {
        TestProgress progress = new TestProgress();

        Assertions.assertThatCode(() -> detector.findDuplicates(new Map<>(), progress))
            .withMessage("Empty directory and map should do nothing.")
            .doesNotThrowAnyException();

        progress.verify(0L, 0L);
    }

    private void testOneFile() {
        TestProgress progress = new TestProgress();

        File file = f.file("file");

        Map<Key, List<File>> map = new Map<>(
            new Pair<>(
                new Key("file", 1L, Algorithm.SHA256, "HASH"),
                new List<>(file)
            )
        );

        detector.findDuplicates(map, progress);

        Assert.assertNull(file.getError());
        progress.verify(1L, 1L);
    }

    private void testUniqueFiles() {
        TestProgress progress = new TestProgress();

        File file1 = f.file("file 1");
        File file2 = f.file("file 2");

        Map<Key, List<File>> map = new Map<>(
            new Pair<>(
                new Key("file 1", 1L, Algorithm.SHA256, "HASH"),
                new List<>(file1)
            ),
            new Pair<>(
                new Key("file 2", 1L, Algorithm.SHA256, "HASH"),
                new List<>(file2)
            )
        );

        detector.findDuplicates(map, progress);

        Assert.assertNull(file1.getError());
        Assert.assertNull(file2.getError());
        progress.verify(2L, 2L);
    }

    private void testMatchingFiles() {
        TestProgress progress = new TestProgress();

        File file1 = f.file("file");
        File file2 = f.file("file");

        Map<Key, List<File>> map = new Map<>(
            new Pair<>(
                new Key("file", 1L, Algorithm.SHA256, "HASH"),
                new List<>(file1, file2)
            )
        );

        detector.findDuplicates(map, progress);

        Exception error1 = file1.getError();
        Assert.assertNotNull(error1);
        Assert.assertInstanceOf(DuplicateException.class, error1);
        Assertions.assertThatCollection(((DuplicateException) error1).getSuspects())
                .withMessage("Duplicate files should be listed in exception.")
                .inAnyOrder()
                .withEqualsFunction(EqualsFunctions.REFERENCE())
                .isEqualTo(new List<>(file1, file2));

        Exception error2 = file2.getError();
        Assert.assertNotNull(error2);
        Assert.assertInstanceOf(DuplicateException.class, error2);
        Assertions.assertThatCollection(((DuplicateException) error2).getSuspects())
            .withMessage("Duplicate files should be listed in exception.")
            .inAnyOrder()
            .withEqualsFunction(EqualsFunctions.REFERENCE())
            .isEqualTo(new List<>(file1, file2));

        progress.verify(1L, 1L);
    }

    private void testMixedFiles() {
        TestProgress progress = new TestProgress();

        File file1 = f.file("file");
        File file2 = f.file("file 2");
        File file3 = f.file("file");

        Map<Key, List<File>> map = new Map<>(
            new Pair<>(
                new Key("file", 1L, Algorithm.SHA256, "HASH"),
                new List<>(file1, file3)
            ),
            new Pair<>(
                new Key("file 2", 1L, Algorithm.SHA256, "HASH"),
                new List<>(file2)
            )
        );

        detector.findDuplicates(map, progress);

        Exception error1 = file1.getError();
        Assert.assertNotNull(error1);
        Assert.assertInstanceOf(DuplicateException.class, error1);
        Assertions.assertThatCollection(((DuplicateException) error1).getSuspects())
            .withMessage("Duplicate files should be listed in exception.")
            .inAnyOrder()
            .withEqualsFunction(EqualsFunctions.REFERENCE())
            .isEqualTo(new List<>(file1, file3));

        Assert.assertNull(file2.getError());

        Exception error3 = file3.getError();
        Assert.assertNotNull(error3);
        Assert.assertInstanceOf(DuplicateException.class, error3);
        Assertions.assertThatCollection(((DuplicateException) error3).getSuspects())
            .withMessage("Duplicate files should be listed in exception.")
            .inAnyOrder()
            .withEqualsFunction(EqualsFunctions.REFERENCE())
            .isEqualTo(new List<>(file1, file3));

        progress.verify(2L, 2L);
    }

    private void testExistingError() {
        TestProgress progress = new TestProgress();

        File file1 = f.file("file");
        file1.setError(new IllegalStateException());
        File file2 = f.file("file");
        file2.setError(new IllegalStateException());

        Map<Key, List<File>> map = new Map<>(
            new Pair<>(
                new Key("file", 1L, Algorithm.SHA256, "HASH"),
                new List<>(file1, file2)
            )
        );

        detector.findDuplicates(map, progress);

        Assert.assertInstanceOf(IllegalStateException.class, file1.getError());
        Assert.assertInstanceOf(IllegalStateException.class, file2.getError());
        progress.verify(1L, 1L);
    }
}
