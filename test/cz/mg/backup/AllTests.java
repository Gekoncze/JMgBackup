package cz.mg.backup;

import cz.mg.backup.gui.services.DirectoryTreeFactoryTest;
import cz.mg.backup.gui.services.SelectionSimplifierTest;
import cz.mg.backup.services.*;

public class AllTests {
    public static void main(String[] args) {
        // cz.mg.backup.gui.services
        DirectoryTreeFactoryTest.main(args);
        SelectionSimplifierTest.main(args);

        // cz.mg.backup.services
        ChecksumReaderTest.main(args);
        ChecksumServiceTest.main(args);
        DirectoryComparatorTest.main(args);
        DirectoryReaderTest.main(args);
        DirectoryServiceTest.main(args);
        DirectorySortTest.main(args);
        FileComparatorTest.main(args);
        FilePropertiesReaderTest.main(args);
        FileReaderTest.main(args);
        HashConverterTest.main(args);
    }
}
