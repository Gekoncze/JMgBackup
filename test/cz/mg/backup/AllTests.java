package cz.mg.backup;

import cz.mg.backup.gui.services.DirectoryTreeFactoryTest;
import cz.mg.backup.services.*;

public class AllTests {
    public static void main(String[] args) {
        // cz.mg.backup.gui.services
        DirectoryTreeFactoryTest.main(args);

        // cz.mg.backup.services
        ChecksumReaderTest.main(args);
        ChecksumManagerTest.main(args);
        DirectoryComparatorTest.main(args);
        DirectoryManagerTest.main(args);
        DirectoryReaderTest.main(args);
        DirectorySearchTest.main(args);
        DirectorySortTest.main(args);
        FileComparatorTest.main(args);
        FileManagerTest.main(args);
        FilePropertiesReaderTest.main(args);
        FileReaderTest.main(args);
        HashConverterTest.main(args);
        TreeIteratorTest.main(args);
    }
}
