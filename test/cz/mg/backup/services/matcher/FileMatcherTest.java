package cz.mg.backup.services.matcher;

import cz.mg.annotations.requirement.Mandatory;
import cz.mg.annotations.requirement.Optional;
import cz.mg.backup.entities.Algorithm;
import cz.mg.backup.entities.Directory;
import cz.mg.backup.entities.File;
import cz.mg.backup.exceptions.DuplicateException;
import cz.mg.backup.exceptions.MoveException;
import cz.mg.backup.test.TestFactory;
import cz.mg.backup.test.TestProgress;
import cz.mg.test.Assert;

import static cz.mg.backup.entities.Algorithm.SHA256;

public class FileMatcherTest {
    public static void main(String[] args) {
        System.out.print("Running " + FileMatcherTest.class.getSimpleName() + " ... ");

        FileMatcherTest test = new FileMatcherTest();
        test.testFindDuplicates();
        test.testFindMoves();

        System.out.println("OK");
    }

    private final @Mandatory FileMatcher detector = FileMatcher.getInstance();
    private final @Mandatory TestFactory f = TestFactory.getInstance();

    private void testFindDuplicates() {
        Directory left = f.directory("dir",
            f.directory("Photos",
                create("Aki.png", 250L, SHA256, "12AB"),
                create("Bunny.png", 320L, SHA256, "2222")
            ),
            f.directory("Images",
                create("Alex.png", 123L, SHA256, "3322"),
                create("Aki.png", 250L, SHA256, "12AB")
            ),
            create("Aki.png", 250L, SHA256, "12AB")
        );
        Directory right = f.directory("dir",
            f.directory("Photos",
                create("Funny.png", 325L, SHA256, "3232"),
                create("Pon.png", 1000L, SHA256, "FFFF")
            ),
            create("Pon.png", 1000L, SHA256, "FFFF"),
            create("Stone.jpg", 111L, SHA256, "4411")
        );
        Converter converter = new Converter(true, true, true, true);

        TestProgress progress = new TestProgress();
        detector.match(left, right, converter, progress);

        Assert.assertNull(left.getError());
        Assert.assertNull(left.getDirectories().get(0).getError());
        Assert.assertInstanceOf(DuplicateException.class, left.getDirectories().get(0).getFiles().get(0).getError());
        Assert.assertNull(left.getDirectories().get(0).getFiles().get(1).getError());
        Assert.assertNull(left.getDirectories().get(1).getError());
        Assert.assertNull(left.getDirectories().get(1).getFiles().get(0).getError());
        Assert.assertInstanceOf(DuplicateException.class, left.getDirectories().get(1).getFiles().get(1).getError());
        Assert.assertInstanceOf(DuplicateException.class, left.getFiles().get(0).getError());

        Assert.assertNull(right.getError());
        Assert.assertNull(right.getDirectories().get(0).getError());
        Assert.assertNull(right.getDirectories().get(0).getFiles().get(0).getError());
        Assert.assertInstanceOf(DuplicateException.class, right.getDirectories().get(0).getFiles().get(1).getError());
        Assert.assertInstanceOf(DuplicateException.class, right.getFiles().get(0).getError());
        Assert.assertNull(right.getFiles().get(1).getError());
    }

    private void testFindMoves() {
        Directory left = f.directory("dir",
            f.directory("Photos",
                create("Aki.png", 250L, SHA256, "12AB"),
                create("Bunny.png", 320L, SHA256, "2222")
            ),
            f.directory("Images",
                create("Alex.png", 123L, SHA256, "3322"),
                create("Pon.png", 1000L, SHA256, "FFFF")
            )
        );
        Directory right = f.directory("dir",
            f.directory("Photos",
                create("Aki.png", 250L, SHA256, "12AB"),
                create("Funny.png", 325L, SHA256, "3232")
            ),
            create("Pon.png", 1000L, SHA256, "FFFF"),
            create("Stone.jpg", 111L, SHA256, "4411")
        );
        Converter converter = new Converter(true, true, true, true);

        TestProgress progress = new TestProgress();
        detector.match(left, right, converter, progress);

        Assert.assertNull(left.getError());
        Assert.assertNull(left.getDirectories().get(0).getError());
        Assert.assertNull(left.getDirectories().get(0).getFiles().get(0).getError());
        Assert.assertNull(left.getDirectories().get(0).getFiles().get(1).getError());
        Assert.assertNull(left.getDirectories().get(1).getError());
        Assert.assertNull(left.getDirectories().get(1).getFiles().get(0).getError());
        Assert.assertInstanceOf(MoveException.class, left.getDirectories().get(1).getFiles().get(1).getError());

        Assert.assertNull(right.getError());
        Assert.assertNull(right.getDirectories().get(0).getError());
        Assert.assertNull(right.getDirectories().get(0).getFiles().get(0).getError());
        Assert.assertNull(right.getDirectories().get(0).getFiles().get(1).getError());
        Assert.assertInstanceOf(MoveException.class, right.getFiles().get(0).getError());
        Assert.assertNull(right.getFiles().get(1).getError());
    }

    private @Mandatory File create(
        @Mandatory String name,
        @Mandatory Long size,
        @Optional Algorithm algorithm,
        @Optional String hash
    ) {
        return f.file(name, f.properties(size), algorithm != null && hash != null ? f.checksum(algorithm, hash) : null);
    }
}
