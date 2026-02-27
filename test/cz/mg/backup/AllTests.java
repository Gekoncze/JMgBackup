package cz.mg.backup;

import cz.mg.backup.services.*;

public class AllTests {
    public static void main(String[] args) {
        // cz.mg.backup.services
        ChecksumManagerTest.main(args);
        ChecksumReaderTest.main(args);
        DirectoryComparatorTest.main(args);
        DirectoryManagerTest.main(args);
        DirectoryPropertiesCollectorTest.main(args);
        DirectoryReaderTest.main(args);
        DirectorySearchTest.main(args);
        DirectorySortTest.main(args);
        FileBackupTest.main(args);
        FileComparatorTest.main(args);
        FileCopyTest.main(args);
        FileManagerTest.main(args);
        FilePropertiesReaderTest.main(args);
        FileReaderTest.main(args);
        HashConverterTest.main(args);
        TreeIteratorTest.main(args);
    }
}
