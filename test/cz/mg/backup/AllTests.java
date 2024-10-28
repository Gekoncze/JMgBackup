package cz.mg.backup;

import cz.mg.backup.gui.services.DirectoryTreeFactoryTest;
import cz.mg.backup.services.*;

public class AllTests {
    public static void main(String[] args) {
        // cz.mg.backup.gui.services
        DirectoryTreeFactoryTest.main(args);

        // cz.mg.backup.services
        ChecksumServiceTest.main(args);
        DirectoryCompareServiceTest.main(args);
        DirectoryErrorServiceTest.main(args);
        DirectoryReaderTest.main(args);
        DirectorySortTest.main(args);
        FileCompareServiceTest.main(args);
        FileHashReaderTest.main(args);
        FileReaderTest.main(args);
        FilePropertiesReaderTest.main(args);
        HashConverterTest.main(args);
    }
}
