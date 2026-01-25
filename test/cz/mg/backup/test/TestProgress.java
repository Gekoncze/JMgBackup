package cz.mg.backup.test;

import cz.mg.annotations.classes.Component;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.backup.components.Progress;
import cz.mg.functions.Predicate;
import cz.mg.test.Assertions;
import cz.mg.test.exceptions.AssertException;

import java.util.Objects;

public @Component class TestProgress extends Progress {
    public static final String MISSING_DESCRIPTION = "<missing description>";
    public static final long MISSING_VALUE = Long.MIN_VALUE;

    public TestProgress() {
        setDescription(MISSING_DESCRIPTION);
        setLimit(MISSING_VALUE);
        setValue(MISSING_VALUE);
    }

    public void verify() {
        if (Objects.equals(getDescription(), MISSING_DESCRIPTION)) {
            throw new AssertException("Missing progress description initialization.");
        }

        if (Objects.equals(getLimit(), MISSING_VALUE)) {
            throw new AssertException("Missing progress limit initialization.");
        }

        if (getValue() < 0) {
            throw new AssertException("Missing progress value initialization.");
        }

        Assertions
            .assertThat(getNext())
            .withMessage("Leftover nested progress.")
            .isNull();
    }

    public void verify(@Mandatory Predicate<Long> limitMatcher, @Mandatory Predicate<Long> valueMatcher) {
        verify();

        Assertions
            .assertThat(getLimit())
            .withMessage("Unexpected progress limit.")
            .matches(limitMatcher);

        Assertions
            .assertThat(getValue())
            .withMessage("Unexpected progress value.")
            .matches(valueMatcher);
    }

    public void verify(long expectedLimit, long expectedValue) {
        verify();

        Assertions
            .assertThat(getLimit())
            .withMessage("Unexpected progress limit.")
            .isEqualTo(expectedLimit);

        Assertions
            .assertThat(getValue())
            .withMessage("Unexpected progress value.")
            .isEqualTo(expectedValue);
    }

    public void verifySkip() {
        if (!Objects.equals(getDescription(), MISSING_DESCRIPTION)) {
            throw new AssertException("Description initialization should have been skipped.");
        }

        if (!Objects.equals(getLimit(), MISSING_VALUE)) {
            throw new AssertException("Limit initialization should have been skipped.");
        }

        if (!Objects.equals(getValue(), MISSING_VALUE)) {
            throw new AssertException("Value initialization should have been skipped.");
        }
    }
}
