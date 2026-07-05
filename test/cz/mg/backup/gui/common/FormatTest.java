package cz.mg.backup.gui.common;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.classes.Test;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.annotations.requirement.Optional;
import cz.mg.backup.components.Progress;
import cz.mg.backup.components.Unit;
import cz.mg.backup.test.TestFactory;
import cz.mg.test.Assert;

public @Test class FormatTest {
    public static void main(String[] args) {
        System.out.print("Running " + FormatTest.class.getSimpleName() + " ... ");

        FormatTest test = new FormatTest();
        test.testFormatInt();
        test.testFormatLong();
        test.testFormatDate();
        test.testFormatProgress();

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

    private void testFormatProgress() {
        Assert.assertEquals("0", Format.format(createProgress(0, 0, null)));
        Assert.assertEquals("0 B", Format.format(createProgress(0, 0, Unit.BYTE)));
        Assert.assertEquals("3", Format.format(createProgress(3, 0, null)));
        Assert.assertEquals("3 B", Format.format(createProgress(3, 0, Unit.BYTE)));
        Assert.assertEquals("0 / 2", Format.format(createProgress(0, 2, null)));
        Assert.assertEquals("0 / 2 B", Format.format(createProgress(0, 2, Unit.BYTE)));
        Assert.assertEquals("7 / 14", Format.format(createProgress(7, 14, null)));
        Assert.assertEquals("7 / 14 B", Format.format(createProgress(7, 14, Unit.BYTE)));
        Assert.assertEquals("2 001 / 2 001", Format.format(createProgress(2001, 2001, null)));
        Assert.assertEquals("2 001 / 2 001 B", Format.format(createProgress(2001, 2001, Unit.BYTE)));
    }

    private @Mandatory Progress createProgress(long value, long limit, @Optional Unit unit) {
        Progress progress = new Progress();
        progress.setValue(value);
        progress.setLimit(limit);
        progress.setUnit(unit);
        return progress;
    }
}
