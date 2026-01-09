package cz.mg.backup.services;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.classes.Test;
import cz.mg.backup.components.Progress;
import cz.mg.backup.entities.Directory;
import cz.mg.backup.entities.File;
import cz.mg.backup.exceptions.CompareException;
import cz.mg.test.Assert;
import cz.mg.test.Assertions;

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
        Progress progress = new Progress("test");

        Assertions.assertThatCode(() -> {
            statisticsCounter.count(null, progress);
        }).doesNotThrowAnyException();

        Assert.assertEquals(0L, progress.getLimit()); // indefinite
        Assert.assertEquals(0L, progress.getValue());
    }

    private void testSingleDirectoryEmpty() {
        Directory directory = new Directory();

        Progress progress = new Progress("test");
        statisticsCounter.count(directory, progress);

        Assert.assertEquals(0L, directory.getProperties().getTotalSize());
        Assert.assertEquals(0L, directory.getProperties().getTotalCount());
        Assert.assertEquals(0L, directory.getProperties().getTotalFileCount());
        Assert.assertEquals(0L, directory.getProperties().getTotalDirectoryCount());
        Assert.assertEquals(0L, directory.getProperties().getTotalErrorCount());
        Assert.assertEquals(0L, progress.getLimit()); // indefinite
        Assert.assertEquals(1L, progress.getValue());
    }

    private void testRecompute() {
        Directory directory = new Directory();
        directory.getErrors().addLast(new CompareException("first error"));
        directory.getErrors().addLast(new CompareException("second error"));
        directory.getProperties().setTotalSize(11111L);
        directory.getProperties().setTotalCount(11111L);
        directory.getProperties().setTotalFileCount(11111L);
        directory.getProperties().setTotalDirectoryCount(11111L);
        directory.getProperties().setTotalErrorCount(11111L);

        statisticsCounter.count(directory, new Progress("test"));

        Assert.assertEquals(0L, directory.getProperties().getTotalSize());
        Assert.assertEquals(0L, directory.getProperties().getTotalCount());
        Assert.assertEquals(0L, directory.getProperties().getTotalFileCount());
        Assert.assertEquals(0L, directory.getProperties().getTotalDirectoryCount());
        Assert.assertEquals(2L, directory.getProperties().getTotalErrorCount());

        statisticsCounter.count(directory, new Progress("test"));

        Assert.assertEquals(0L, directory.getProperties().getTotalSize());
        Assert.assertEquals(0L, directory.getProperties().getTotalCount());
        Assert.assertEquals(0L, directory.getProperties().getTotalFileCount());
        Assert.assertEquals(0L, directory.getProperties().getTotalDirectoryCount());
        Assert.assertEquals(2L, directory.getProperties().getTotalErrorCount());
    }

    private void testSingleDirectorySingleFile() {
        File file = new File();
        file.getProperties().setSize(123L);
        file.getErrors().addLast(new CompareException("third error"));
        file.getErrors().addLast(new FileSystemException("fourth error"));
        file.getErrors().addLast(new RuntimeException("fifth error"));

        Directory directory = new Directory();
        directory.getErrors().addLast(new CompareException("first error"));
        directory.getErrors().addLast(new CompareException("second error"));
        directory.getFiles().addLast(file);

        Progress progress = new Progress("test");
        statisticsCounter.count(directory, progress);

        Assert.assertEquals(123L, directory.getProperties().getTotalSize());
        Assert.assertEquals(1L, directory.getProperties().getTotalCount());
        Assert.assertEquals(1L, directory.getProperties().getTotalFileCount());
        Assert.assertEquals(0L, directory.getProperties().getTotalDirectoryCount());
        Assert.assertEquals(5L, directory.getProperties().getTotalErrorCount());
        Assert.assertEquals(0L, progress.getLimit()); // indefinite
        Assert.assertEquals(2L, progress.getValue());
    }

    private void testSingleDirectorySingleSubdirectory() {
        Directory subDirectory = new Directory();
        subDirectory.getProperties().setTotalSize(11111L);
        subDirectory.getProperties().setTotalCount(11111L);
        subDirectory.getProperties().setTotalFileCount(11111L);
        subDirectory.getProperties().setTotalDirectoryCount(11111L);
        subDirectory.getProperties().setTotalErrorCount(11111L);
        subDirectory.getErrors().addLast(new CompareException("third error"));

        Directory directory = new Directory();
        directory.getErrors().addLast(new CompareException("first error"));
        directory.getErrors().addLast(new CompareException("second error"));
        directory.getDirectories().addLast(subDirectory);

        Progress progress = new Progress("test");
        statisticsCounter.count(directory, progress);

        Assert.assertEquals(0L, directory.getProperties().getTotalSize());
        Assert.assertEquals(1L, directory.getProperties().getTotalCount());
        Assert.assertEquals(0L, directory.getProperties().getTotalFileCount());
        Assert.assertEquals(1L, directory.getProperties().getTotalDirectoryCount());
        Assert.assertEquals(3L, directory.getProperties().getTotalErrorCount());

        Assert.assertEquals(0L, subDirectory.getProperties().getTotalSize());
        Assert.assertEquals(0L, subDirectory.getProperties().getTotalCount());
        Assert.assertEquals(0L, subDirectory.getProperties().getTotalFileCount());
        Assert.assertEquals(0L, subDirectory.getProperties().getTotalDirectoryCount());
        Assert.assertEquals(1L, subDirectory.getProperties().getTotalErrorCount());

        Assert.assertEquals(0L, progress.getLimit()); // indefinite
        Assert.assertEquals(2L, progress.getValue());
    }

    private void testMultipleNested() {
        File subDirectoryFile = new File();
        subDirectoryFile.getProperties().setSize(123L);
        subDirectoryFile.getErrors().addLast(new FileSystemException("fourth error"));
        subDirectoryFile.getErrors().addLast(new RuntimeException("fifth error"));
        subDirectoryFile.getErrors().addLast(new CompareException("sixth error"));

        File directoryFile = new File();
        directoryFile.getProperties().setSize(7L);

        Directory subDirectory = new Directory();
        subDirectory.getErrors().addLast(new CompareException("third error"));
        subDirectory.getFiles().addLast(subDirectoryFile);

        Directory directory = new Directory();
        directory.getErrors().addLast(new CompareException("first error"));
        directory.getErrors().addLast(new CompareException("second error"));
        directory.getDirectories().addLast(subDirectory);
        directory.getFiles().addLast(directoryFile);

        Progress progress = new Progress("test");
        statisticsCounter.count(directory, progress);

        Assert.assertEquals(130L, directory.getProperties().getTotalSize());
        Assert.assertEquals(3L, directory.getProperties().getTotalCount());
        Assert.assertEquals(2L, directory.getProperties().getTotalFileCount());
        Assert.assertEquals(1L, directory.getProperties().getTotalDirectoryCount());
        Assert.assertEquals(6L, directory.getProperties().getTotalErrorCount());

        Assert.assertEquals(123L, subDirectory.getProperties().getTotalSize());
        Assert.assertEquals(1L, subDirectory.getProperties().getTotalCount());
        Assert.assertEquals(1L, subDirectory.getProperties().getTotalFileCount());
        Assert.assertEquals(0L, subDirectory.getProperties().getTotalDirectoryCount());
        Assert.assertEquals(4L, subDirectory.getProperties().getTotalErrorCount());

        Assert.assertEquals(0L, progress.getLimit()); // indefinite
        Assert.assertEquals(4L, progress.getValue());
    }
}
