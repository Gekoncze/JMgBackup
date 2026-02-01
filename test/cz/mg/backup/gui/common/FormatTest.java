package cz.mg.backup.gui.common;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.classes.Test;
import cz.mg.backup.test.TestFactory;
import cz.mg.test.Assert;

public @Test class FormatTest {
    public static void main(String[] args) {
        System.out.print("Running " + FormatTest.class.getSimpleName() + " ... ");

        FormatTest test = new FormatTest();
        test.testFormatInt();
        test.testFormatLong();
        test.testFormatDate();

        System.out.println("OK");
    }

    private final @Service TestFactory f = TestFactory.getInstance();

    private void testFormatInt() {
        Assert.assertEquals("1", Format.format(1));
        Assert.assertEquals("10", Format.format(10));
        Assert.assertEquals("100", Format.format(100));
        Assert.assertEquals("1 000", Format.format(1000));
        Assert.assertEquals("10 000", Format.format(10000));
        Assert.assertEquals("100 000", Format.format(100000));
        Assert.assertEquals("1 000 000", Format.format(1000000));
    }

    private void testFormatLong() {
        Assert.assertEquals("1", Format.format(1L));
        Assert.assertEquals("10", Format.format(10L));
        Assert.assertEquals("100", Format.format(100L));
        Assert.assertEquals("1 000", Format.format(1000L));
        Assert.assertEquals("10 000", Format.format(10000L));
        Assert.assertEquals("100 000", Format.format(100000L));
        Assert.assertEquals("1 000 000", Format.format(1000000L));
    }

    private void testFormatDate() {
        Assert.assertEquals(
            "01. 02. 2026, 13:21",
            Format.format(f.date(2026, 2, 1, 13, 21))
        );
    }
}
