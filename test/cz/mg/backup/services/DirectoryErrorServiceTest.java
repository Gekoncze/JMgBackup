package cz.mg.backup.services;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.classes.Test;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.backup.entities.Directory;
import cz.mg.backup.entities.File;
import cz.mg.backup.exceptions.PropagatedException;
import cz.mg.collections.list.List;
import cz.mg.test.Assert;

import java.nio.file.Path;

public @Test class DirectoryErrorServiceTest {
    public static void main(String[] args) {
        System.out.print("Running " + DirectoryErrorServiceTest.class.getSimpleName() + " ... ");

        DirectoryErrorServiceTest test = new DirectoryErrorServiceTest();
        test.testPropagate();

        System.out.println("OK");
    }

    private final @Service DirectoryErrorService service = DirectoryErrorService.getInstance();

    private void testPropagate() {
        Directory l3 = createDirectory("L3");
        l3.getErrors().addLast(new RuntimeException());

        Directory l2 = createDirectory(
            "L2",
            new List<>(l3),
            new List<>()
        );

        Directory l2a = createDirectory("L2a");
        Directory l2b = createDirectory("L2b");

        Directory l1 = createDirectory(
            "L1",
            new List<>(l2a, l2, l2b),
            new List<>()
        );

        service.propagate(l1);

        Assert.assertEquals(false, l1.getErrors().isEmpty());
        Assert.assertEquals(true, l2a.getErrors().isEmpty());
        Assert.assertEquals(false, l2.getErrors().isEmpty());
        Assert.assertEquals(true, l2b.getErrors().isEmpty());
        Assert.assertEquals(false, l3.getErrors().isEmpty());
        Assert.assertEquals(PropagatedException.class, l1.getErrors().getFirst().getClass());

        l3.getErrors().clear();

        service.propagate(l1);

        Assert.assertEquals(true, l1.getErrors().isEmpty());
        Assert.assertEquals(true, l2a.getErrors().isEmpty());
        Assert.assertEquals(true, l2.getErrors().isEmpty());
        Assert.assertEquals(true, l2b.getErrors().isEmpty());
        Assert.assertEquals(true, l3.getErrors().isEmpty());
    }

    private @Mandatory Directory createDirectory(@Mandatory String name) {
        return createDirectory(name, new List<>(), new List<>());
    }

    private @Mandatory Directory createDirectory(
        @Mandatory String name,
        @Mandatory List<Directory> directories,
        @Mandatory List<File> files
    ) {
        Directory directory = new Directory();
        directory.setPath(Path.of(name));
        directory.setDirectories(directories);
        directory.setFiles(files);
        return directory;
    }
}
