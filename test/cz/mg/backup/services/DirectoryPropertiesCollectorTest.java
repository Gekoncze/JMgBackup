package cz.mg.backup.services;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.classes.Test;
import cz.mg.backup.entities.Directory;
import cz.mg.backup.entities.File;
import cz.mg.backup.test.TestFactory;
import cz.mg.test.Assert;

public @Test class DirectoryPropertiesCollectorTest {
    public static void main(String[] args) {
        System.out.print("Running " + DirectoryPropertiesCollectorTest.class.getSimpleName() + " ... ");

        DirectoryPropertiesCollectorTest test = new DirectoryPropertiesCollectorTest();
        test.testCollectEmpty();
        test.testCollect();

        System.out.println("OK");
    }

    private final @Service DirectoryPropertiesCollector propertiesCollector = DirectoryPropertiesCollector.getInstance();
    private final @Service TestFactory f = TestFactory.getInstance();

    private void testCollectEmpty() {
        Directory directory = f.directory("foo");
        directory.getProperties().setTotalSize(123L);
        directory.getProperties().setTotalCount(123L);
        directory.getProperties().setTotalFileCount(123L);
        directory.getProperties().setTotalDirectoryCount(123L);

        propertiesCollector.collect(directory);

        Assert.assertEquals(0L, directory.getProperties().getTotalSize());
        Assert.assertEquals(0L, directory.getProperties().getTotalCount());
        Assert.assertEquals(0L, directory.getProperties().getTotalFileCount());
        Assert.assertEquals(0L, directory.getProperties().getTotalDirectoryCount());
    }

    private void testCollect() {
        Directory subdirectory = f.directory("bar");
        subdirectory.getProperties().setTotalSize(2L);
        subdirectory.getProperties().setTotalCount(2200L);
        subdirectory.getProperties().setTotalFileCount(200L);
        subdirectory.getProperties().setTotalDirectoryCount(2000L);

        File file = f.file("foobar");
        file.getProperties().setSize(3L);

        Directory directory = f.directory("foo", file, subdirectory);

        propertiesCollector.collect(directory);

        Assert.assertEquals(5L, directory.getProperties().getTotalSize());
        Assert.assertEquals(2202L, directory.getProperties().getTotalCount());
        Assert.assertEquals(201L, directory.getProperties().getTotalFileCount());
        Assert.assertEquals(2001L, directory.getProperties().getTotalDirectoryCount());

        subdirectory.getProperties().setTotalSize(7L);
        subdirectory.getProperties().setTotalCount(4400L);
        subdirectory.getProperties().setTotalFileCount(400L);
        subdirectory.getProperties().setTotalDirectoryCount(4000L);
        file.getProperties().setSize(9L);

        propertiesCollector.collect(directory);

        Assert.assertEquals(16L, directory.getProperties().getTotalSize());
        Assert.assertEquals(4402L, directory.getProperties().getTotalCount());
        Assert.assertEquals(401L, directory.getProperties().getTotalFileCount());
        Assert.assertEquals(4001L, directory.getProperties().getTotalDirectoryCount());
    }
}
