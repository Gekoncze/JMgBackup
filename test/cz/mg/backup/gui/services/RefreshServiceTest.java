package cz.mg.backup.gui.services;

import cz.mg.annotations.classes.Test;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.backup.entities.Directory;
import cz.mg.backup.entities.File;
import cz.mg.backup.exceptions.MissingException;
import cz.mg.backup.gui.entities.State;
import cz.mg.backup.test.TestFactory;
import cz.mg.backup.test.TestProgress;
import cz.mg.test.Assert;

public @Test class RefreshServiceTest {
    public static void main(String[] args) {
        System.out.print("Running " + RefreshServiceTest.class.getSimpleName() + " ... ");

        RefreshServiceTest test = new RefreshServiceTest();
        test.testCompared();
        test.testUpdateDetailsFoundLeft();
        test.testUpdateDetailsFoundRight();
        test.testUpdateDetailsNotFound();
        test.testNullDetails();

        System.out.println("OK");
    }

    private final @Mandatory RefreshService refreshService = RefreshService.getInstance();
    private final @Mandatory TestFactory f = TestFactory.getInstance();

    private void testCompared() {
        File file = f.file("foo");
        Directory left = f.directory("left", file);
        Directory right = f.directory("right");
        State state = new State(left, right, null);

        TestProgress progress = new TestProgress();
        refreshService.refresh(state, progress);

        Assert.assertSame(left, state.getLeft());
        Assert.assertSame(right, state.getRight());
        Assert.assertNotNull(file.getError());
        Assert.assertEquals(MissingException.class, file.getError().getClass());
        progress.verify();
    }

    private void testUpdateDetailsFoundLeft() {
        File newFile = f.file("foo");
        Directory left = f.directory("left", newFile);
        Directory right = f.directory("right");
        File oldFile = new File();
        oldFile.setPath(newFile.getPath());
        oldFile.setRelativePath(newFile.getRelativePath());
        State state = new State(left, right, oldFile);

        TestProgress progress = new TestProgress();
        refreshService.refresh(state, progress);

        Assert.assertSame(newFile, state.getDetails());
        progress.verify();
    }

    private void testUpdateDetailsFoundRight() {
        File newFile = f.file("foo");
        Directory left = f.directory("left");
        Directory right = f.directory("right", newFile);
        File oldFile = new File();
        oldFile.setPath(newFile.getPath());
        oldFile.setRelativePath(newFile.getRelativePath());
        State state = new State(left, right, oldFile);

        TestProgress progress = new TestProgress();
        refreshService.refresh(state, progress);

        Assert.assertSame(newFile, state.getDetails());
        progress.verify();
    }

    private void testUpdateDetailsNotFound() {
        Directory left = f.directory("left");
        Directory right = f.directory("right");
        File oldFile = f.file("x");
        State state = new State(left, right, oldFile);

        TestProgress progress = new TestProgress();
        refreshService.refresh(state, progress);

        Assert.assertNull(state.getDetails());
        progress.verify();
    }

    private void testNullDetails() {
        Directory left = f.directory("left");
        Directory right = f.directory("right");
        State state = new State(left, right, null);

        TestProgress progress = new TestProgress();
        refreshService.refresh(state, progress);

        Assert.assertNull(state.getDetails());
        progress.verify();
    }
}
