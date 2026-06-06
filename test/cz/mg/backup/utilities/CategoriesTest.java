package cz.mg.backup.utilities;

import cz.mg.annotations.classes.Test;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.backup.exceptions.CategorizedException;
import cz.mg.backup.exceptions.Category;
import cz.mg.backup.test.TestFactory;
import cz.mg.test.Assert;

public @Test class CategoriesTest {
    public static void main(String[] args) {
        System.out.print("Running " + CategoriesTest.class.getSimpleName() + " ... ");

        CategoriesTest test = new CategoriesTest();
        test.testGetNull();
        test.testGetCategorized();
        test.testGetDefault();
        test.testGetInnerEmpty();
        test.testGetInnerOne();
        test.testGetInnerSame();
        test.testGetInnerPrecedence();

        System.out.println("OK");
    }

    private final @Mandatory TestFactory f = TestFactory.getInstance();

    private void testGetNull() {
        Assert.assertEquals(
            null,
            Categories.get(f.file("foo", null))
        );
    }

    private void testGetCategorized() {
        Assert.assertEquals(
            Category.WARNING,
            Categories.get(f.file("foo", new TestException(Category.WARNING)))
        );
    }

    private void testGetDefault() {
        Assert.assertEquals(
            Category.ERROR,
            Categories.get(f.file("foo", new RuntimeException()))
        );
    }

    private void testGetInnerEmpty() {
        Assert.assertEquals(
            null,
            Categories.getInner(f.directory("bar"))
        );
    }

    private void testGetInnerOne() {
        Assert.assertEquals(
            null,
            Categories.getInner(f.directory("bar", f.file("foo")))
        );

        Assert.assertEquals(
            Category.ERROR,
            Categories.getInner(f.directory("bar", f.file("foo", new RuntimeException())))
        );
    }

    private void testGetInnerSame() {
        Assert.assertEquals(
            Category.INFORMATION,
            Categories.getInner(f.directory(
                "bar",
                f.file("foo1", new TestException(Category.INFORMATION)),
                f.directory("foo2", new TestException(Category.INFORMATION)),
                f.file("foo3", new TestException(Category.INFORMATION))
            ))
        );
    }

    private void testGetInnerPrecedence() {
        Assert.assertEquals(
            Category.ERROR,
            Categories.getInner(f.directory(
                "bar",
                f.file("foo0"),
                f.file("foo1", new TestException(Category.PROBLEM)),
                f.directory("foo1", new TestException(Category.WARNING)),
                f.file("foo2", new TestException(Category.ERROR)),
                f.directory("foo3", new TestException(Category.INFORMATION))
            ))
        );

        Assert.assertEquals(
            Category.PROBLEM,
            Categories.getInner(f.directory(
                "bar",
                f.file("foo0"),
                f.directory("foo1", new TestException(Category.PROBLEM)),
                f.file("foo1", new TestException(Category.WARNING)),
                f.directory("foo3", new TestException(Category.INFORMATION))
            ))
        );

        Assert.assertEquals(
            Category.WARNING,
            Categories.getInner(f.directory(
                "bar",
                f.file("foo0"),
                f.directory("foo1", new TestException(Category.WARNING)),
                f.directory("foo3", new TestException(Category.INFORMATION))
            ))
        );

        Assert.assertEquals(
            Category.INFORMATION,
            Categories.getInner(f.directory(
                "bar",
                f.file("foo0"),
                f.directory("foo3", new TestException(Category.INFORMATION))
            ))
        );

        Assert.assertEquals(
            Category.INFORMATION,
            Categories.getInner(f.directory(
                "bar",
                f.file("foo0"),
                f.file("foo3", new TestException(Category.INFORMATION))
            ))
        );
    }

    private static class TestException extends RuntimeException implements CategorizedException {
        private final @Mandatory Category category;

        public TestException(@Mandatory Category category) {
            this.category = category;
        }

        @Override
        public @Mandatory Category getCategory() {
            return category;
        }
    }
}
