package cz.mg.backup.services.matcher;

import cz.mg.annotations.classes.Test;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.backup.entities.Algorithm;
import cz.mg.backup.entities.File;
import cz.mg.backup.exceptions.MissingException;
import cz.mg.backup.exceptions.MoveException;
import cz.mg.backup.test.TestFactory;
import cz.mg.backup.test.TestProgress;
import cz.mg.collections.list.List;
import cz.mg.collections.map.Map;
import cz.mg.collections.pair.Pair;
import cz.mg.test.Assert;
import cz.mg.test.Assertions;

public @Test class MoveDetectorTest {
    public static void main(String[] args) {
        System.out.print("Running " + MoveDetectorTest.class.getSimpleName() + " ... ");

        MoveDetectorTest test = new MoveDetectorTest();
        test.testEmpty();
        test.testTargetEmpty();
        test.testSourceEmpty();
        test.testDifferentFiles();
        test.testNoMove();
        test.testMove();
        test.testMoveRoot();
        test.testMoveButDuplicateSource();
        test.testMoveButDuplicateTarget();
        test.testExistingExceptionSource();
        test.testExistingExceptionTarget();
        test.testExistingExceptionSourceOverride();
        test.testExistingExceptionTargetOverride();

        System.out.println("OK");
    }

    private final @Mandatory MoveDetector detector = MoveDetector.getInstance();
    private final @Mandatory TestFactory f = TestFactory.getInstance();

    private void testEmpty() {
        TestProgress progress = new TestProgress();

        Assertions.assertThatCode(() -> detector.findMoves(createMap(), createMap(), progress))
            .withMessage("Empty maps should do nothing.")
            .doesNotThrowAnyException();

        progress.verify(0L, 0L);
    }

    private void testTargetEmpty() {
        TestProgress progress = new TestProgress();
        File source = f.file("root/dir/source");
        Map<Key, List<File>> sourceMap = createMap(
            new Pair<>(
                new Key("source", 1L, Algorithm.MD5, "HASH"),
                new List<>(source)
            )
        );

        detector.findMoves(sourceMap, createMap(), progress);

        Assert.assertNull(source.getException());
        progress.verify(1L, 1L);
    }

    private void testSourceEmpty() {
        TestProgress progress = new TestProgress();
        File target = f.file("root/dir/target");
        Map<Key, List<File>> targetMap = createMap(
            new Pair<>(
                new Key("target", 1L, Algorithm.MD5, "HASH"),
                new List<>(target)
            )
        );

        detector.findMoves(createMap(), targetMap, progress);

        Assert.assertNull(target.getException());
        progress.verify(0L, 0L);
    }

    private void testDifferentFiles() {
        TestProgress progress = new TestProgress();
        File source = f.file("root/dir/source");
        Map<Key, List<File>> sourceMap = createMap(
            new Pair<>(
                new Key("source", 1L, Algorithm.MD5, "HASH"),
                new List<>(source)
            )
        );
        File target = f.file("root/dir/target");
        Map<Key, List<File>> targetMap = createMap(
            new Pair<>(
                new Key("target", 1L, Algorithm.MD5, "HASH"),
                new List<>(target)
            )
        );

        detector.findMoves(sourceMap, targetMap, progress);

        Assert.assertNull(source.getException());
        Assert.assertNull(target.getException());
        progress.verify(1L, 1L);
    }

    private void testNoMove() {
        TestProgress progress = new TestProgress();
        File source = f.file("root/dir/file");
        Map<Key, List<File>> sourceMap = createMap(
            new Pair<>(
                new Key("file", 1L, Algorithm.MD5, "HASH"),
                new List<>(source)
            )
        );
        File target = f.file("root/dir/file");
        Map<Key, List<File>> targetMap = createMap(
            new Pair<>(
                new Key("file", 1L, Algorithm.MD5, "HASH"),
                new List<>(target)
            )
        );

        detector.findMoves(sourceMap, targetMap, progress);

        Assert.assertNull(source.getException());
        Assert.assertNull(target.getException());
        progress.verify(1L, 1L);
    }

    private void testMove() {
        TestProgress progress = new TestProgress();
        File source = f.file("root/foo/file");
        Map<Key, List<File>> sourceMap = createMap(
            new Pair<>(
                new Key("file", 1L, Algorithm.MD5, "HASH"),
                new List<>(source)
            )
        );
        File target = f.file("root/bar/file");
        Map<Key, List<File>> targetMap = createMap(
            new Pair<>(
                new Key("file", 1L, Algorithm.MD5, "HASH"),
                new List<>(target)
            )
        );

        detector.findMoves(sourceMap, targetMap, progress);

        Exception sourceException = source.getException();
        Assert.assertNotNull(sourceException);
        Assert.assertInstanceOf(MoveException.class, sourceException);
        Assert.assertSame(((MoveException)sourceException).getSuspect(), target);

        Exception targetException = target.getException();
        Assert.assertNotNull(targetException);
        Assert.assertInstanceOf(MoveException.class, targetException);
        Assert.assertSame(((MoveException)targetException).getSuspect(), source);

        progress.verify(1L, 1L);
    }

    private void testMoveRoot() {
        TestProgress progress = new TestProgress();
        File source = f.file("root_A/foo/file");
        Map<Key, List<File>> sourceMap = createMap(
            new Pair<>(
                new Key("file", 1L, Algorithm.MD5, "HASH"),
                new List<>(source)
            )
        );
        File target = f.file("root_B/bar/file");
        Map<Key, List<File>> targetMap = createMap(
            new Pair<>(
                new Key("file", 1L, Algorithm.MD5, "HASH"),
                new List<>(target)
            )
        );

        detector.findMoves(sourceMap, targetMap, progress);

        Exception sourceException = source.getException();
        Assert.assertNotNull(sourceException);
        Assert.assertInstanceOf(MoveException.class, sourceException);
        Assert.assertSame(((MoveException)sourceException).getSuspect(), target);

        Exception targetException = target.getException();
        Assert.assertNotNull(targetException);
        Assert.assertInstanceOf(MoveException.class, targetException);
        Assert.assertSame(((MoveException)targetException).getSuspect(), source);

        progress.verify(1L, 1L);
    }

    private void testMoveButDuplicateSource() {
        TestProgress progress = new TestProgress();
        File source = f.file("root/foo/file");
        Map<Key, List<File>> sourceMap = createMap(
            new Pair<>(
                new Key("file", 1L, Algorithm.MD5, "HASH"),
                new List<>(source, f.file("root/foo/foo/file"))
            )
        );
        File target = f.file("root/bar/file");
        Map<Key, List<File>> targetMap = createMap(
            new Pair<>(
                new Key("file", 1L, Algorithm.MD5, "HASH"),
                new List<>(target)
            )
        );

        detector.findMoves(sourceMap, targetMap, progress);

        Assert.assertNull(source.getException());
        Assert.assertNull(target.getException());
        progress.verify(1L, 1L);
    }

    private void testMoveButDuplicateTarget() {
        TestProgress progress = new TestProgress();
        File source = f.file("root/foo/file");
        Map<Key, List<File>> sourceMap = createMap(
            new Pair<>(
                new Key("file", 1L, Algorithm.MD5, "HASH"),
                new List<>(source)
            )
        );
        File target = f.file("root/bar/file");
        Map<Key, List<File>> targetMap = createMap(
            new Pair<>(
                new Key("file", 1L, Algorithm.MD5, "HASH"),
                new List<>(target, f.file("root/bar/bar/file"))
            )
        );

        detector.findMoves(sourceMap, targetMap, progress);

        Assert.assertNull(source.getException());
        Assert.assertNull(target.getException());
        progress.verify(1L, 1L);
    }

    private void testExistingExceptionSource() {
        TestProgress progress = new TestProgress();
        File source = f.file("root/foo/file");
        source.setException(new IllegalStateException());
        Map<Key, List<File>> sourceMap = createMap(
            new Pair<>(
                new Key("file", 1L, Algorithm.MD5, "HASH"),
                new List<>(source)
            )
        );
        File target = f.file("root/bar/file");
        Map<Key, List<File>> targetMap = createMap(
            new Pair<>(
                new Key("file", 1L, Algorithm.MD5, "HASH"),
                new List<>(target)
            )
        );

        detector.findMoves(sourceMap, targetMap, progress);

        Assert.assertInstanceOf(IllegalStateException.class, source.getException());
        Assert.assertInstanceOf(MoveException.class, target.getException());
        progress.verify(1L, 1L);
    }

    private void testExistingExceptionTarget() {
        TestProgress progress = new TestProgress();
        File source = f.file("root/foo/file");
        Map<Key, List<File>> sourceMap = createMap(
            new Pair<>(
                new Key("file", 1L, Algorithm.MD5, "HASH"),
                new List<>(source)
            )
        );
        File target = f.file("root/bar/file");
        target.setException(new IllegalStateException());
        Map<Key, List<File>> targetMap = createMap(
            new Pair<>(
                new Key("file", 1L, Algorithm.MD5, "HASH"),
                new List<>(target)
            )
        );

        detector.findMoves(sourceMap, targetMap, progress);

        Assert.assertInstanceOf(MoveException.class, source.getException());
        Assert.assertInstanceOf(IllegalStateException.class, target.getException());
        progress.verify(1L, 1L);
    }

    private void testExistingExceptionSourceOverride() {
        TestProgress progress = new TestProgress();
        File source = f.file("root/foo/file");
        source.setException(new MissingException("Missing"));
        Map<Key, List<File>> sourceMap = createMap(
            new Pair<>(
                new Key("file", 1L, Algorithm.MD5, "HASH"),
                new List<>(source)
            )
        );
        File target = f.file("root/bar/file");
        Map<Key, List<File>> targetMap = createMap(
            new Pair<>(
                new Key("file", 1L, Algorithm.MD5, "HASH"),
                new List<>(target)
            )
        );

        detector.findMoves(sourceMap, targetMap, progress);

        Assert.assertInstanceOf(MoveException.class, source.getException());
        Assert.assertInstanceOf(MoveException.class, target.getException());
        progress.verify(1L, 1L);
    }

    private void testExistingExceptionTargetOverride() {
        TestProgress progress = new TestProgress();
        File source = f.file("root/foo/file");
        Map<Key, List<File>> sourceMap = createMap(
            new Pair<>(
                new Key("file", 1L, Algorithm.MD5, "HASH"),
                new List<>(source)
            )
        );
        File target = f.file("root/bar/file");
        target.setException(new MissingException("Missing"));
        Map<Key, List<File>> targetMap = createMap(
            new Pair<>(
                new Key("file", 1L, Algorithm.MD5, "HASH"),
                new List<>(target)
            )
        );

        detector.findMoves(sourceMap, targetMap, progress);

        Assert.assertInstanceOf(MoveException.class, source.getException());
        Assert.assertInstanceOf(MoveException.class, target.getException());
        progress.verify(1L, 1L);
    }

    @SafeVarargs
    private @Mandatory Map<Key, List<File>> createMap(Pair<Key, List<File>>... pairs) {
        KeyComparator comparator = new KeyComparator();
        Map<Key, List<File>> map = new Map<>(new List<>(pairs), comparator, comparator);
        return map;
    }
}
