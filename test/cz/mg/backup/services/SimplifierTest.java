package cz.mg.backup.services;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.classes.Test;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.backup.components.Progress;
import cz.mg.backup.entities.Node;
import cz.mg.collections.list.List;
import cz.mg.test.Assert;

import java.nio.file.Path;

public @Test class SimplifierTest {
    public static void main(String[] args) {
        System.out.print("Running " + SimplifierTest.class.getSimpleName() + " ... ");

        SimplifierTest test = new SimplifierTest();
        test.testEmpty();
        test.testRootSingle();
        test.testRootMultiple();
        test.testRootAllDuplicates();
        test.testRootSomeDuplicates();
        test.testChildrenWithSelectedParentFirst();
        test.testChildrenWithSelectedParentMiddle();
        test.testChildrenWithSelectedParentLast();
        test.testChildrenWithoutSelectedParent();
        test.testChildWithDistantSelectedParent();
        test.testNestedSingle();
        test.testNestedMultiple();
        test.testNestedAllDuplicates();
        test.testNestedSomeDuplicates();
        test.testNestedChildrenWithSelectedParent();
        test.testMixed();

        System.out.println("OK");
    }

    private final @Service Simplifier simplifier = Simplifier.getInstance();

    private void testEmpty() {
        List<Node> input = new List<>();

        Progress progress = new Progress("test");
        List<Node> output = simplifier.simplify(input, progress);

        Assert.assertEquals(true, output.isEmpty());
        Assert.assertEquals(0L, progress.getLimit()); // indefinite
        Assert.assertEquals(0L, progress.getValue()); // indefinite
    }

    private void testRootSingle() {
        List<Node> input = new List<>(
            create("cat")
        );

        Progress progress = new Progress("test");
        List<Node> output = simplifier.simplify(input, progress);

        Assert.assertEquals(1, output.count());
        Assert.assertEquals("cat", output.get(0).getPath().toString());
        Assert.assertEquals(0L, progress.getLimit()); // indefinite
        Assert.assertEquals(true, progress.getValue() > 1L);
    }

    private void testRootMultiple() {
        List<Node> input = new List<>(
            create("cat"),
            create("dog"),
            create("pangolin")
        );

        Progress progress = new Progress("test");
        List<Node> output = simplifier.simplify(input, progress);

        Assert.assertEquals(3, output.count());
        Assert.assertEquals("cat", output.get(0).getPath().toString());
        Assert.assertEquals("dog", output.get(1).getPath().toString());
        Assert.assertEquals("pangolin", output.get(2).getPath().toString());
        Assert.assertEquals(0L, progress.getLimit()); // indefinite
        Assert.assertEquals(true, progress.getValue() > 3L);
    }

    private void testRootAllDuplicates() {
        List<Node> input = new List<>(
            create("cat"),
            create("cat")
        );

        Progress progress = new Progress("test");
        List<Node> output = simplifier.simplify(input, progress);

        Assert.assertEquals(1, output.count());
        Assert.assertEquals("cat", output.get(0).getPath().toString());
        Assert.assertEquals(0L, progress.getLimit()); // indefinite
        Assert.assertEquals(true, progress.getValue() > 2L);
    }

    private void testRootSomeDuplicates() {
        List<Node> input = new List<>(
            create("cat"),
            create("dog"),
            create("cat")
        );

        Progress progress = new Progress("test");
        List<Node> output = simplifier.simplify(input, progress);

        Assert.assertEquals(2, output.count());
        Assert.assertEquals("cat", output.get(0).getPath().toString());
        Assert.assertEquals("dog", output.get(1).getPath().toString());
        Assert.assertEquals(0L, progress.getLimit()); // indefinite
        Assert.assertEquals(true, progress.getValue() > 3L);
    }

    private void testChildrenWithSelectedParentFirst() {
        List<Node> input = new List<>(
            create("cat"),
            create("cat", "yellow"),
            create("cat", "gray")
        );

        Progress progress = new Progress("test");
        List<Node> output = simplifier.simplify(input, progress);

        Assert.assertEquals(1, output.count());
        Assert.assertEquals("cat", output.get(0).getPath().toString());
        Assert.assertEquals(0L, progress.getLimit()); // indefinite
        Assert.assertEquals(true, progress.getValue() > 5L);
    }

    private void testChildrenWithSelectedParentMiddle() {
        List<Node> input = new List<>(
            create("cat", "yellow"),
            create("cat"),
            create("cat", "gray")
        );

        Progress progress = new Progress("test");
        List<Node> output = simplifier.simplify(input, progress);

        Assert.assertEquals(1, output.count());
        Assert.assertEquals("cat", output.get(0).getPath().toString());
        Assert.assertEquals(0L, progress.getLimit()); // indefinite
        Assert.assertEquals(true, progress.getValue() > 5L);
    }

    private void testChildrenWithSelectedParentLast() {
        List<Node> input = new List<>(
            create("cat", "yellow"),
            create("cat", "gray"),
            create("cat")
        );

        Progress progress = new Progress("test");
        List<Node> output = simplifier.simplify(input, progress);

        Assert.assertEquals(1, output.count());
        Assert.assertEquals("cat", output.get(0).getPath().toString());
        Assert.assertEquals(0L, progress.getLimit()); // indefinite
        Assert.assertEquals(true, progress.getValue() > 5L);
    }

    private void testChildrenWithoutSelectedParent() {
        List<Node> input = new List<>(
            create("cat", "yellow"),
            create("cat", "gray"),
            create("cat", "black")
        );

        Progress progress = new Progress("test");
        List<Node> output = simplifier.simplify(input, progress);

        Assert.assertEquals(3, output.count());
        Assert.assertEquals("cat/yellow", output.get(0).getPath().toString());
        Assert.assertEquals("cat/gray", output.get(1).getPath().toString());
        Assert.assertEquals("cat/black", output.get(2).getPath().toString());
        Assert.assertEquals(0L, progress.getLimit()); // indefinite
        Assert.assertEquals(true, progress.getValue() > 6L);
    }

    private void testChildWithDistantSelectedParent() {
        List<Node> input = new List<>(
            create("cat"),
            create("cat", "yellow", "sleepy", "rare")
        );

        Progress progress = new Progress("test");
        List<Node> output = simplifier.simplify(input, progress);

        Assert.assertEquals(1, output.count());
        Assert.assertEquals("cat", output.get(0).getPath().toString());
        Assert.assertEquals(0L, progress.getLimit()); // indefinite
        Assert.assertEquals(true, progress.getValue() > 5L);
    }

    private void testNestedSingle() {
        List<Node> input = new List<>(
            create("cat", "yellow")
        );

        Progress progress = new Progress("test");
        List<Node> output = simplifier.simplify(input, progress);

        Assert.assertEquals(1, output.count());
        Assert.assertEquals("cat/yellow", output.get(0).getPath().toString());
        Assert.assertEquals(0L, progress.getLimit()); // indefinite
        Assert.assertEquals(true, progress.getValue() > 2L);
    }

    private void testNestedMultiple() {
        List<Node> input = new List<>(
            create("cat", "yellow"),
            create("dog", "gray"),
            create("pangolin", "brown")
        );

        Progress progress = new Progress("test");
        List<Node> output = simplifier.simplify(input, progress);

        Assert.assertEquals(3, output.count());
        Assert.assertEquals("cat/yellow", output.get(0).getPath().toString());
        Assert.assertEquals("dog/gray", output.get(1).getPath().toString());
        Assert.assertEquals("pangolin/brown", output.get(2).getPath().toString());
        Assert.assertEquals(0L, progress.getLimit()); // indefinite
        Assert.assertEquals(true, progress.getValue() > 6L);
    }

    private void testNestedAllDuplicates() {
        List<Node> input = new List<>(
            create("pangolin", "brown"),
            create("pangolin", "brown"),
            create("pangolin", "brown")
        );

        Progress progress = new Progress("test");
        List<Node> output = simplifier.simplify(input, progress);

        Assert.assertEquals(1, output.count());
        Assert.assertEquals("pangolin/brown", output.get(0).getPath().toString());
        Assert.assertEquals(0L, progress.getLimit()); // indefinite
        Assert.assertEquals(true, progress.getValue() > 6L);
    }

    private void testNestedSomeDuplicates() {
        List<Node> input = new List<>(
            create("pangolin", "white"),
            create("pangolin", "brown"),
            create("pangolin", "brown")
        );

        Progress progress = new Progress("test");
        List<Node> output = simplifier.simplify(input, progress);

        Assert.assertEquals(2, output.count());
        Assert.assertEquals("pangolin/white", output.get(0).getPath().toString());
        Assert.assertEquals("pangolin/brown", output.get(1).getPath().toString());
        Assert.assertEquals(0L, progress.getLimit()); // indefinite
        Assert.assertEquals(true, progress.getValue() > 6L);
    }

    private void testNestedChildrenWithSelectedParent() {
        List<Node> input = new List<>(
            create("animal", "cat"),
            create("animal", "cat", "yellow"),
            create("animal", "cat", "gray")
        );

        Progress progress = new Progress("test");
        List<Node> output = simplifier.simplify(input, progress);

        Assert.assertEquals(1, output.count());
        Assert.assertEquals("animal/cat", output.get(0).getPath().toString());
        Assert.assertEquals(0L, progress.getLimit()); // indefinite
        Assert.assertEquals(true, progress.getValue() > 8L);
    }

    private void testMixed() {
        List<Node> input = new List<>(
            create("dog", "white"),
            create("dog", "gray"),
            create("dog", "gray", "small"),
            create("dog", "gray", "big"),
            create("dog", "gray", "big", "happy"),
            create("cat"),
            create("pangolin", "brown"),
            create("pangolin", "brown"),
            create("pangolin", "white"),
            create("bird", "blue", "small"),
            create("fox", "orange", "medium", "sleepy", "rare")
        );

        Progress progress = new Progress("test");
        List<Node> output = simplifier.simplify(input, progress);

        Assert.assertEquals(7, output.count());
        Assert.assertEquals("dog/white", output.get(0).getPath().toString());
        Assert.assertEquals("dog/gray", output.get(1).getPath().toString());
        Assert.assertEquals("cat", output.get(2).getPath().toString());
        Assert.assertEquals("pangolin/brown", output.get(3).getPath().toString());
        Assert.assertEquals("pangolin/white", output.get(4).getPath().toString());
        Assert.assertEquals("bird/blue/small", output.get(5).getPath().toString());
        Assert.assertEquals("fox/orange/medium/sleepy/rare", output.get(6).getPath().toString());
        Assert.assertEquals(0L, progress.getLimit()); // indefinite
        Assert.assertEquals(true, progress.getValue() > 29L);
    }

    private @Mandatory Node create(String first, String... path) {
        Node node = new Node();
        node.setPath(Path.of(first, path));
        return node;
    }
}
