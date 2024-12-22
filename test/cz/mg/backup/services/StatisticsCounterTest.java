package cz.mg.backup.services;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.classes.Test;
import cz.mg.backup.entities.Directory;
import cz.mg.backup.entities.DirectoryProperties;
import cz.mg.backup.entities.File;
import cz.mg.backup.entities.FileProperties;
import cz.mg.backup.exceptions.CompareException;
import cz.mg.test.Assert;

import java.nio.file.FileSystemException;

public @Test class StatisticsCounterTest {
    public static void main(String[] args) {
        System.out.print("Running " + StatisticsCounterTest.class.getSimpleName() + " ... ");

        StatisticsCounterTest test = new StatisticsCounterTest();
        test.testEmpty();
        test.testSingleDirectoryEmpty();
        test.testRecompute();
        test.testSingleDirectorySingleFile();
        test.testSingleDirectorySingleSubdirectory();
        test.testMultipleNested();

        System.out.println("OK");
    }

    private final @Service StatisticsCounter statisticsCounter = StatisticsCounter.getInstance();

    private void testEmpty() {
        Assert.assertThatCode(() -> {
            statisticsCounter.count(null);
        }).doesNotThrowAnyException();
    }

    private void testSingleDirectoryEmpty() {
        Directory directory = new Directory();
        directory.setProperties(new DirectoryProperties());

        statisticsCounter.count(directory);

        Assert.assertEquals(0L, directory.getProperties().getTotalSize());
        Assert.assertEquals(0, directory.getProperties().getTotalCount());
        Assert.assertEquals(0, directory.getProperties().getTotalFileCount());
        Assert.assertEquals(0, directory.getProperties().getTotalDirectoryCount());
        Assert.assertEquals(0, directory.getProperties().getTotalErrorCount());
    }

    private void testRecompute() {
        Directory directory = new Directory();
        directory.setProperties(new DirectoryProperties());
        directory.getErrors().addLast(new CompareException("first error"));
        directory.getErrors().addLast(new CompareException("second error"));
        directory.getProperties().setTotalSize(11111L);
        directory.getProperties().setTotalCount(11111);
        directory.getProperties().setTotalFileCount(11111);
        directory.getProperties().setTotalDirectoryCount(11111);
        directory.getProperties().setTotalErrorCount(11111);

        statisticsCounter.count(directory);

        Assert.assertEquals(0L, directory.getProperties().getTotalSize());
        Assert.assertEquals(0, directory.getProperties().getTotalCount());
        Assert.assertEquals(0, directory.getProperties().getTotalFileCount());
        Assert.assertEquals(0, directory.getProperties().getTotalDirectoryCount());
        Assert.assertEquals(2, directory.getProperties().getTotalErrorCount());

        statisticsCounter.count(directory);

        Assert.assertEquals(0L, directory.getProperties().getTotalSize());
        Assert.assertEquals(0, directory.getProperties().getTotalCount());
        Assert.assertEquals(0, directory.getProperties().getTotalFileCount());
        Assert.assertEquals(0, directory.getProperties().getTotalDirectoryCount());
        Assert.assertEquals(2, directory.getProperties().getTotalErrorCount());
    }

    private void testSingleDirectorySingleFile() {
        File file = new File();
        file.setProperties(new FileProperties());
        file.getProperties().setSize(123L);
        file.getErrors().addLast(new CompareException("third error"));
        file.getErrors().addLast(new FileSystemException("fourth error"));
        file.getErrors().addLast(new RuntimeException("fifth error"));

        Directory directory = new Directory();
        directory.setProperties(new DirectoryProperties());
        directory.getErrors().addLast(new CompareException("first error"));
        directory.getErrors().addLast(new CompareException("second error"));
        directory.getFiles().addLast(file);

        statisticsCounter.count(directory);

        Assert.assertEquals(123L, directory.getProperties().getTotalSize());
        Assert.assertEquals(1, directory.getProperties().getTotalCount());
        Assert.assertEquals(1, directory.getProperties().getTotalFileCount());
        Assert.assertEquals(0, directory.getProperties().getTotalDirectoryCount());
        Assert.assertEquals(5, directory.getProperties().getTotalErrorCount());
    }

    private void testSingleDirectorySingleSubdirectory() {
        Directory subDirectory = new Directory();
        subDirectory.setProperties(new DirectoryProperties());
        subDirectory.getProperties().setTotalSize(11111L);
        subDirectory.getProperties().setTotalCount(11111);
        subDirectory.getProperties().setTotalFileCount(11111);
        subDirectory.getProperties().setTotalDirectoryCount(11111);
        subDirectory.getProperties().setTotalErrorCount(11111);
        subDirectory.getErrors().addLast(new CompareException("third error"));

        Directory directory = new Directory();
        directory.setProperties(new DirectoryProperties());
        directory.getErrors().addLast(new CompareException("first error"));
        directory.getErrors().addLast(new CompareException("second error"));
        directory.getDirectories().addLast(subDirectory);

        statisticsCounter.count(directory);

        Assert.assertEquals(0L, directory.getProperties().getTotalSize());
        Assert.assertEquals(1, directory.getProperties().getTotalCount());
        Assert.assertEquals(0, directory.getProperties().getTotalFileCount());
        Assert.assertEquals(1, directory.getProperties().getTotalDirectoryCount());
        Assert.assertEquals(3, directory.getProperties().getTotalErrorCount());

        Assert.assertEquals(0L, subDirectory.getProperties().getTotalSize());
        Assert.assertEquals(0, subDirectory.getProperties().getTotalCount());
        Assert.assertEquals(0, subDirectory.getProperties().getTotalFileCount());
        Assert.assertEquals(0, subDirectory.getProperties().getTotalDirectoryCount());
        Assert.assertEquals(1, subDirectory.getProperties().getTotalErrorCount());
    }

    private void testMultipleNested() {
        File subDirectoryFile = new File();
        subDirectoryFile.setProperties(new FileProperties());
        subDirectoryFile.getProperties().setSize(123L);
        subDirectoryFile.getErrors().addLast(new FileSystemException("fourth error"));
        subDirectoryFile.getErrors().addLast(new RuntimeException("fifth error"));
        subDirectoryFile.getErrors().addLast(new CompareException("sixth error"));

        File directoryFile = new File();
        directoryFile.setProperties(new FileProperties());
        directoryFile.getProperties().setSize(7L);

        Directory subDirectory = new Directory();
        subDirectory.setProperties(new DirectoryProperties());
        subDirectory.getErrors().addLast(new CompareException("third error"));
        subDirectory.getFiles().addLast(subDirectoryFile);

        Directory directory = new Directory();
        directory.setProperties(new DirectoryProperties());
        directory.getErrors().addLast(new CompareException("first error"));
        directory.getErrors().addLast(new CompareException("second error"));
        directory.getDirectories().addLast(subDirectory);
        directory.getFiles().addLast(directoryFile);

        statisticsCounter.count(directory);

        Assert.assertEquals(130L, directory.getProperties().getTotalSize());
        Assert.assertEquals(3, directory.getProperties().getTotalCount());
        Assert.assertEquals(2, directory.getProperties().getTotalFileCount());
        Assert.assertEquals(1, directory.getProperties().getTotalDirectoryCount());
        Assert.assertEquals(6, directory.getProperties().getTotalErrorCount());

        Assert.assertEquals(123L, subDirectory.getProperties().getTotalSize());
        Assert.assertEquals(1, subDirectory.getProperties().getTotalCount());
        Assert.assertEquals(1, subDirectory.getProperties().getTotalFileCount());
        Assert.assertEquals(0, subDirectory.getProperties().getTotalDirectoryCount());
        Assert.assertEquals(4, subDirectory.getProperties().getTotalErrorCount());
    }
}
