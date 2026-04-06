package cz.mg.backup.services.matcher;

import cz.mg.annotations.requirement.Mandatory;
import cz.mg.annotations.requirement.Optional;
import cz.mg.backup.entities.Algorithm;
import cz.mg.backup.entities.File;
import cz.mg.backup.test.TestFactory;

import static cz.mg.backup.entities.Algorithm.*;
import static cz.mg.test.Assertions.assertThat;

public class ConverterTest {
    public static void main(String[] args) {
        System.out.print("Running " + ConverterTest.class.getSimpleName() + " ... ");

        ConverterTest test = new ConverterTest();
        test.testConvertNothing();
        test.testConvertName();
        test.testConvertType();
        test.testConvertNameAndType();
        test.testConvertSize();
        test.testConvertChecksum();
        test.testConvertAll();

        System.out.println("OK");
    }

    private final @Mandatory TestFactory f = TestFactory.getInstance();
    private final @Mandatory KeyComparator comparator = new KeyComparator();

    private void testConvertNothing() {
        assertThat(new Converter(false, false, false, false).convert(create("FooBar.txt", 7L, null, null)))
            .withMessage("Everything should be null.")
            .withFormatFunction(this::formatKey)
            .withEqualsFunction(comparator)
            .isEqualTo(new Key(null, null, null, null));

        assertThat(new Converter(false, false, false, false).convert(create("FooBar.txt", 7L, MD5, "ABC")))
            .withMessage("Everything should be null.")
            .withFormatFunction(this::formatKey)
            .withEqualsFunction(comparator)
            .isEqualTo(new Key(null, null, null, null));
    }

    private void testConvertName() {
        assertThat(new Converter(true, false, false, false).convert(f.file("FooBar.txt")))
            .withMessage("File name should exclude extension.")
            .withFormatFunction(this::formatKey)
            .withEqualsFunction(comparator)
            .isEqualTo(new Key("FooBar", null, null, null));

        assertThat(new Converter(true, false, false, false).convert(f.file("FooBar")))
            .withMessage("File name without extension should not change.")
            .withFormatFunction(this::formatKey)
            .withEqualsFunction(comparator)
            .isEqualTo(new Key("FooBar", null, null, null));

        assertThat(new Converter(true, false, false, false).convert(f.file(".FooBar")))
            .withMessage("Hidden file name should not change.")
            .withFormatFunction(this::formatKey)
            .withEqualsFunction(comparator)
            .isEqualTo(new Key(".FooBar", null, null, null));

        assertThat(new Converter(true, false, false, false).convert(f.file(".FooBar.txt")))
            .withMessage("Hidden file name should exclude extension.")
            .withFormatFunction(this::formatKey)
            .withEqualsFunction(comparator)
            .isEqualTo(new Key(".FooBar", null, null, null));
    }

    private void testConvertType() {
        assertThat(new Converter(false, true, false, false).convert(f.file("FooBar.txt")))
            .withMessage("Only extension should be included.")
            .withFormatFunction(this::formatKey)
            .withEqualsFunction(comparator)
            .isEqualTo(new Key("txt", null, null, null));

        assertThat(new Converter(false, true, false, false).convert(f.file("FooBar")))
            .withMessage("Missing extension should convert to null.")
            .withFormatFunction(this::formatKey)
            .withEqualsFunction(comparator)
            .isEqualTo(new Key(null, null, null, null));

        assertThat(new Converter(false, true, false, false).convert(f.file(".FooBar")))
            .withMessage("Missing extension for hidden file should convert to null.")
            .withFormatFunction(this::formatKey)
            .withEqualsFunction(comparator)
            .isEqualTo(new Key(null, null, null, null));

        assertThat(new Converter(false, true, false, false).convert(f.file(".FooBar.txt")))
            .withMessage("Only extension should be included for hidden file.")
            .withFormatFunction(this::formatKey)
            .withEqualsFunction(comparator)
            .isEqualTo(new Key("txt", null, null, null));
    }

    private void testConvertNameAndType() {
        assertThat(new Converter(true, true, false, false).convert(f.file("FooBar.txt")))
            .withMessage("File name should not change.")
            .withFormatFunction(this::formatKey)
            .withEqualsFunction(comparator)
            .isEqualTo(new Key("FooBar.txt", null, null, null));

        assertThat(new Converter(true, true, false, false).convert(f.file("FooBar")))
            .withMessage("File name should not change.")
            .withFormatFunction(this::formatKey)
            .withEqualsFunction(comparator)
            .isEqualTo(new Key("FooBar", null, null, null));

        assertThat(new Converter(true, true, false, false).convert(f.file(".FooBar")))
            .withMessage("File name should not change.")
            .withFormatFunction(this::formatKey)
            .withEqualsFunction(comparator)
            .isEqualTo(new Key(".FooBar", null, null, null));

        assertThat(new Converter(true, true, false, false).convert(f.file(".FooBar.txt")))
            .withMessage("File name should not change.")
            .withFormatFunction(this::formatKey)
            .withEqualsFunction(comparator)
            .isEqualTo(new Key(".FooBar.txt", null, null, null));
    }

    private void testConvertSize() {
        assertThat(new Converter(false, false, true, false).convert(create(".FooBar.txt", 7L, null, null)))
            .withMessage("Size should not change.")
            .withFormatFunction(this::formatKey)
            .withEqualsFunction(comparator)
            .isEqualTo(new Key(null, 7L, null, null));
    }

    private void testConvertChecksum() {
        assertThat(new Converter(false, false, false, true).convert(create(".FooBar.txt", 7L, MD5, "ABC")))
            .withMessage("Checksum should not change.")
            .withFormatFunction(this::formatKey)
            .withEqualsFunction(comparator)
            .isEqualTo(new Key(null, null, MD5, "ABC"));
    }

    private void testConvertAll() {
        assertThat(new Converter(true, true, true, true).convert(create(".FooBar.txt", 7L, MD5, "ABC")))
            .withMessage("Nothing should change.")
            .withFormatFunction(this::formatKey)
            .withEqualsFunction(comparator)
            .isEqualTo(new Key(".FooBar.txt", 7L, MD5, "ABC"));
    }

    private @Mandatory String formatKey(@Mandatory Key key) {
        return "[" + key.getName() + ", " + key.getSize() + ", " + key.getAlgorithm() + ", " + key.getHash() + "]";
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
