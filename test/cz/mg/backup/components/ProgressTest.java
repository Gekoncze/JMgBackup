package cz.mg.backup.components;

import cz.mg.annotations.classes.Test;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.backup.exceptions.CancelException;
import cz.mg.backup.test.TestFactory;
import cz.mg.test.Assert;
import cz.mg.test.Assertions;

import java.util.Objects;

public @Test class ProgressTest {
    public static void main(String[] args) {
        System.out.print("Running " + ProgressTest.class.getSimpleName() + " ... ");

        ProgressTest test = new ProgressTest();
        test.testInitialize();
        test.testStep();
        test.testNest();
        test.testUnnest();
        test.testCheckStatus();

        System.out.println("OK");
    }

    private final @Mandatory TestFactory f = TestFactory.getInstance();

    private void testInitialize() {
        Progress progress = new Progress();
        progress.setDescription("Old description");
        progress.setLimit(77L);
        progress.setValue(7L);
        progress.setNext(new Progress());

        progress.initialize("New description", f.file("foo/bar.txt"), 11L);

        Assert.assertEquals("New description: bar.txt", progress.getDescription());
        Assert.assertEquals(11L, progress.getLimit());
        Assert.assertEquals(0, progress.getValue());
        Assert.assertEquals(null, progress.getNext());

        progress.initialize("Final description", null, 22L);

        Assert.assertEquals("Final description", progress.getDescription());
        Assert.assertEquals(22L, progress.getLimit());
        Assert.assertEquals(0, progress.getValue());
        Assert.assertEquals(null, progress.getNext());
    }

    private void testStep()
    {
        Progress progress = new Progress();
        Assert.assertEquals(0L, progress.getValue());
        progress.step();
        Assert.assertEquals(1L, progress.getValue());
        progress.step();
        Assert.assertEquals(2L, progress.getValue());
        progress.step(7L);
        Assert.assertEquals(9L, progress.getValue());
    }

    private void testNest() {
        Progress progress = new Progress();
        Assert.assertEquals(null, progress.getNext());
        Progress nested = progress.nest();
        Assert.assertEquals(nested, progress.getNext());
        Progress overwritten = progress.nest();
        Assert.assertEquals(overwritten, progress.getNext());
    }

    private void testUnnest() {
        Progress progress = new Progress();
        Assert.assertEquals(null, progress.getNext());
        Assert.assertEquals(null, progress.unnest());
        Progress nested = new Progress();
        progress.setNext(nested);
        Assert.assertEquals(nested, progress.getNext());
        progress.unnest();
        Assert.assertEquals(null, progress.getNext());
    }

    private void testCheckStatus() {
        boolean[] success = {false};

        Task<?> task = new Task<>(() -> {
            Task<?> t = Objects.requireNonNull(Task.getCurrentTask());
            Progress progress = t.getProgress();

            Assertions.assertThatCode(() -> progress.checkStatus())
                    .withMessage("Check status should not throw exception when task is not cancelled.")
                    .doesNotThrowAnyException();

            t.cancel();

            Assertions.assertThatCode(() -> progress.checkStatus())
                .withMessage("Check status should throw cancel exception when task is cancelled.")
                .throwsException(CancelException.class);

            success[0] = true;
        });

        task.start();
        task.join();

        Assert.assertEquals(success[0], true);
        Assert.assertEquals(task.getException(), null);
    }
}
