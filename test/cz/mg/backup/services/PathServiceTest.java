package cz.mg.backup.services;

import cz.mg.annotations.classes.Test;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.test.Assert;

import java.nio.file.Path;

public @Test class PathServiceTest {
    public static void main(String[] args) {
        System.out.print("Running " + PathServiceTest.class.getSimpleName() + " ... ");

        PathServiceTest test = new PathServiceTest();
        test.testEmpty();
        test.testOne();
        test.testTwo();
        test.testThree();

        System.out.println("OK");
    }

    private final @Mandatory PathService pathService = PathService.getInstance();

    private void testEmpty() {
        Assert.assertEquals(
            Path.of(""),
            pathService.removeLeadingPart(Path.of(""))
        );
    }

    private void testOne() {
        Assert.assertEquals(
            Path.of(""),
            pathService.removeLeadingPart(Path.of("foo"))
        );
    }

    private void testTwo() {
        Assert.assertEquals(
            Path.of("bar"),
            pathService.removeLeadingPart(Path.of("foo").resolve("bar"))
        );
    }

    private void testThree() {
        Assert.assertEquals(
            Path.of("bar").resolve("123"),
            pathService.removeLeadingPart(Path.of("foo").resolve("bar").resolve("123"))
        );
    }
}
