package cz.mg.backup.gui.services;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.classes.Test;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.backup.components.Progress;
import cz.mg.backup.entities.Node;
import cz.mg.collections.list.List;
import cz.mg.test.Assert;

import java.nio.file.Path;

public @Test class SelectionSimplifierTest {
    public static void main(String[] args) {
        System.out.print("Running " + SelectionSimplifierTest.class.getSimpleName() + " ... ");

        SelectionSimplifierTest test = new SelectionSimplifierTest();
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

    private final @Service SelectionSimplifier simplifier = SelectionSimplifier.getInstance();

    private void testEmpty() {
        List<Node> input = new List<>();

        List<Node> output = simplifier.simplify(input, new Progress("test"));

        Assert.assertEquals(true, output.isEmpty());
    }

    private void testRootSingle() {
        List<Node> input = new List<>(
            create("cat")
        );

        List<Node> output = simplifier.simplify(input, new Progress("test"));

        Assert.assertEquals(1, output.count());
        Assert.assertEquals("cat", output.get(0).getPath().toString());
    }

    private void testRootMultiple() {
        List<Node> input = new List<>(
            create("cat"),
            create("dog"),
            create("pangolin")
        );

        List<Node> output = simplifier.simplify(input, new Progress("test"));

        Assert.assertEquals(3, output.count());
        Assert.assertEquals("cat", output.get(0).getPath().toString());
        Assert.assertEquals("dog", output.get(1).getPath().toString());
        Assert.assertEquals("pangolin", output.get(2).getPath().toString());
    }

    private void testRootAllDuplicates() {
        List<Node> input = new List<>(
            create("cat"),
            create("cat")
        );

        List<Node> output = simplifier.simplify(input, new Progress("test"));

        Assert.assertEquals(1, output.count());
        Assert.assertEquals("cat", output.get(0).getPath().toString());
    }

    private void testRootSomeDuplicates() {
        List<Node> input = new List<>(
            create("cat"),
            create("dog"),
            create("cat")
        );

        List<Node> output = simplifier.simplify(input, new Progress("test"));

        Assert.assertEquals(2, output.count());
        Assert.assertEquals("cat", output.get(0).getPath().toString());
        Assert.assertEquals("dog", output.get(1).getPath().toString());
    }

    private void testChildrenWithSelectedParentFirst() {
        List<Node> input = new List<>(
            create("cat"),
            create("cat", "yellow"),
            create("cat", "gray")
        );

        List<Node> output = simplifier.simplify(input, new Progress("test"));

        Assert.assertEquals(1, output.count());
        Assert.assertEquals("cat", output.get(0).getPath().toString());
    }

    private void testChildrenWithSelectedParentMiddle() {
        List<Node> input = new List<>(
            create("cat", "yellow"),
            create("cat"),
            create("cat", "gray")
        );

        List<Node> output = simplifier.simplify(input, new Progress("test"));

        Assert.assertEquals(1, output.count());
        Assert.assertEquals("cat", output.get(0).getPath().toString());
    }

    private void testChildrenWithSelectedParentLast() {
        List<Node> input = new List<>(
            create("cat", "yellow"),
            create("cat", "gray"),
            create("cat")
        );

        List<Node> output = simplifier.simplify(input, new Progress("test"));

        Assert.assertEquals(1, output.count());
        Assert.assertEquals("cat", output.get(0).getPath().toString());
    }

    private void testChildrenWithoutSelectedParent() {
        List<Node> input = new List<>(
            create("cat", "yellow"),
            create("cat", "gray"),
            create("cat", "black")
        );

        List<Node> output = simplifier.simplify(input, new Progress("test"));

        Assert.assertEquals(3, output.count());
        Assert.assertEquals("cat/yellow", output.get(0).getPath().toString());
        Assert.assertEquals("cat/gray", output.get(1).getPath().toString());
        Assert.assertEquals("cat/black", output.get(2).getPath().toString());
    }

    private void testChildWithDistantSelectedParent() {
        List<Node> input = new List<>(
            create("cat"),
            create("cat", "yellow", "sleepy", "rare")
        );

        List<Node> output = simplifier.simplify(input, new Progress("test"));

        Assert.assertEquals(1, output.count());
        Assert.assertEquals("cat", output.get(0).getPath().toString());
    }

    private void testNestedSingle() {
        List<Node> input = new List<>(
            create("cat", "yellow")
        );

        List<Node> output = simplifier.simplify(input, new Progress("test"));

        Assert.assertEquals(1, output.count());
        Assert.assertEquals("cat/yellow", output.get(0).getPath().toString());
    }

    private void testNestedMultiple() {
        List<Node> input = new List<>(
            create("cat", "yellow"),
            create("dog", "gray"),
            create("pangolin", "brown")
        );

        List<Node> output = simplifier.simplify(input, new Progress("test"));

        Assert.assertEquals(3, output.count());
        Assert.assertEquals("cat/yellow", output.get(0).getPath().toString());
        Assert.assertEquals("dog/gray", output.get(1).getPath().toString());
        Assert.assertEquals("pangolin/brown", output.get(2).getPath().toString());
    }

    private void testNestedAllDuplicates() {
        List<Node> input = new List<>(
            create("pangolin", "brown"),
            create("pangolin", "brown"),
            create("pangolin", "brown")
        );

        List<Node> output = simplifier.simplify(input, new Progress("test"));

        Assert.assertEquals(1, output.count());
        Assert.assertEquals("pangolin/brown", output.get(0).getPath().toString());
    }

    private void testNestedSomeDuplicates() {
        List<Node> input = new List<>(
            create("pangolin", "white"),
            create("pangolin", "brown"),
            create("pangolin", "brown")
        );

        List<Node> output = simplifier.simplify(input, new Progress("test"));

        Assert.assertEquals(2, output.count());
        Assert.assertEquals("pangolin/white", output.get(0).getPath().toString());
        Assert.assertEquals("pangolin/brown", output.get(1).getPath().toString());
    }

    private void testNestedChildrenWithSelectedParent() {
        List<Node> input = new List<>(
            create("animal", "cat"),
            create("animal", "cat", "yellow"),
            create("animal", "cat", "gray")
        );

        List<Node> output = simplifier.simplify(input, new Progress("test"));

        Assert.assertEquals(1, output.count());
        Assert.assertEquals("animal/cat", output.get(0).getPath().toString());
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

        List<Node> output = simplifier.simplify(input, new Progress("test"));

        Assert.assertEquals(7, output.count());
        Assert.assertEquals("dog/white", output.get(0).getPath().toString());
        Assert.assertEquals("dog/gray", output.get(1).getPath().toString());
        Assert.assertEquals("cat", output.get(2).getPath().toString());
        Assert.assertEquals("pangolin/brown", output.get(3).getPath().toString());
        Assert.assertEquals("pangolin/white", output.get(4).getPath().toString());
        Assert.assertEquals("bird/blue/small", output.get(5).getPath().toString());
        Assert.assertEquals("fox/orange/medium/sleepy/rare", output.get(6).getPath().toString());
    }

    private @Mandatory Node create(String first, String... path) {
        Node node = new Node();
        node.setPath(Path.of(first, path));
        return node;
    }
}
