package cz.mg.backup;

import cz.mg.backup.gui.services.RefreshServiceTest;
import cz.mg.backup.services.*;

public class AllTests {
    public static void main(String[] args) {
        // cz.mg.backup.services
        BackupServiceTest.main(args);
        ChecksumManagerTest.main(args);
        ChecksumReaderTest.main(args);
        DirectoryComparatorTest.main(args);
        DirectoryPropertiesCollectorTest.main(args);
        DirectoryReaderTest.main(args);
        DirectoryReloaderTest.main(args);
        DirectorySearchTest.main(args);
        DirectorySortTest.main(args);
        DirectoryWriterTest.main(args);
        FileComparatorTest.main(args);
        FileCopyTest.main(args);
        FileManagerTest.main(args);
        FilePropertiesReaderTest.main(args);
        FileReaderTest.main(args);
        HashConverterTest.main(args);
        PathServiceTest.main(args);
        TreeIteratorTest.main(args);

        // cz.mg.backup.gui.services
        RefreshServiceTest.main(args);
    }
}
