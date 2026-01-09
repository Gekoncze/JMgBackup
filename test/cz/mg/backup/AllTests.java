package cz.mg.backup;

import cz.mg.backup.gui.services.DirectoryTreeFactoryTest;
import cz.mg.backup.services.SimplifierTest;
import cz.mg.backup.services.*;

public class AllTests {
    public static void main(String[] args) {
        // cz.mg.backup.gui.services
        DirectoryTreeFactoryTest.main(args);

        // cz.mg.backup.services
        ChecksumReaderTest.main(args);
        ChecksumServiceTest.main(args);
        DirectoryComparatorTest.main(args);
        DirectoryManagerTest.main(args);
        DirectoryReaderTest.main(args);
        DirectorySearchTest.main(args);
        DirectorySortTest.main(args);
        FileComparatorTest.main(args);
        FileCopyTest.main(args);
        FilePropertiesReaderTest.main(args);
        FileReaderTest.main(args);
        HashConverterTest.main(args);
        PathConverterTest.main(args);
        SimplifierTest.main(args);
        StatisticsCounterTest.main(args);
        TreeIteratorTest.main(args);
    }
}
