package cz.mg.backup;

import cz.mg.backup.services.*;

public class AllTests {
    public static void main(String[] args) {
        DirectoryReaderTest.main(args);
        DirectorySortTest.main(args);
        FileHashConverterTest.main(args);
        FileHashReaderTest.main(args);
        FileReaderTest.main(args);
        FileSizeReaderTest.main(args);
    }
}
