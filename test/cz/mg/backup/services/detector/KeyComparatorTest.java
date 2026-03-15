package cz.mg.backup.services.detector;

import cz.mg.annotations.requirement.Mandatory;
import cz.mg.backup.entities.Algorithm;
import cz.mg.test.Assert;

public class KeyComparatorTest {
    public static void main(String[] args) {
        System.out.print("Running " + KeyComparatorTest.class.getSimpleName() + " ... ");

        KeyComparatorTest test = new KeyComparatorTest();
        test.testEquals();
        test.testHash();

        System.out.println("OK");
    }

    private final @Mandatory KeyComparator comparator = new KeyComparator();

    private void testEquals() {
        Assert.assertEquals(true, comparator.equals(
            new Key(null, null, null, null),
            new Key(null, null, null, null)
        ));

        Assert.assertEquals(true, comparator.equals(
            new Key("foo", 7L, Algorithm.MD5, "ABCD"),
            new Key("foo", 7L, Algorithm.MD5, "ABCD")
        ));

        Assert.assertEquals(false, comparator.equals(
            new Key("foo", 7L, Algorithm.MD5, "ABCD"),
            new Key("foo", null, Algorithm.MD5, "ABCD")
        ));

        Assert.assertEquals(false, comparator.equals(
            new Key("foo", 7L, Algorithm.MD5, "ABCD"),
            new Key("foo", 7L, Algorithm.MD5, "ABCA")
        ));
    }

    private void testHash() {
        Assert.assertEquals(
            comparator.hash(new Key(null, null, null, null)),
            comparator.hash(new Key(null, null, null, null))
        );

        Assert.assertEquals(
            comparator.hash(new Key("foo", 7L, Algorithm.MD5, "ABCD")),
            comparator.hash(new Key("foo", 7L, Algorithm.MD5, "ABCD"))
        );

        Assert.assertNotEquals(
            comparator.hash(new Key("foo", 7L, Algorithm.MD5, "ABCD")),
            comparator.hash(new Key("foo", null, Algorithm.MD5, "ABCD"))
        );

        Assert.assertNotEquals(
            comparator.hash(new Key("foo", 7L, Algorithm.MD5, "ABCD")),
            comparator.hash(new Key("foo", 7L, Algorithm.MD5, "ABCA"))
        );
    }
}
