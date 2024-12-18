package cz.mg.backup.services;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.classes.Test;
import cz.mg.test.Assert;

public @Test class HashConverterTest {
    public static void main(String[] args) {
        System.out.print("Running " + HashConverterTest.class.getSimpleName() + " ... ");

        HashConverterTest test = new HashConverterTest();
        test.testConvertEmpty();
        test.testConvertSingle();
        test.testConvertMultiple();

        System.out.println("OK");
    }

    private final @Service HashConverter converter = HashConverter.getInstance();

    private void testConvertEmpty() {
        Assert.assertEquals("", converter.convert(new byte[]{}));
    }

    private void testConvertSingle() {
        Assert.assertEquals("ff", converter.convert(new byte[]{-1}));
    }

    private void testConvertMultiple() {
        Assert.assertEquals(
            "000102070a0ffffe",
            converter.convert(new byte[]{0, 1, 2, 7, 10, 15, -1, -2})
        );
    }
}
