package cz.mg.backup.test;

import cz.mg.annotations.classes.Component;
import cz.mg.backup.components.Progress;
import cz.mg.test.Assertions;
import cz.mg.test.exceptions.AssertException;

import java.util.Objects;

public @Component class TestProgress extends Progress {
    public static final String MISSING_DESCRIPTION = "<missing description>";
    public static final long MISSING_VALUE = -2L;

    public TestProgress() {
        setDescription(MISSING_DESCRIPTION);
        setLimit(MISSING_VALUE);
        setValue(MISSING_VALUE);
    }

    public void verify(long expectedLimit, long expectedValue) {
        if (Objects.equals(getDescription(), MISSING_DESCRIPTION)) {
            throw new AssertException("Missing progress description.");
        }

        if (Objects.equals(getLimit(), MISSING_VALUE)) {
            throw new AssertException("Missing progress limit.");
        }

        if (Objects.equals(getValue(), MISSING_VALUE)) {
            throw new AssertException("Missing progress value.");
        }

        Assertions
            .assertThat(getLimit())
            .withMessage("Unexpected progress limit.")
            .isEqualTo(expectedLimit);

        Assertions
            .assertThat(getValue())
            .withMessage("Unexpected progress value.")
            .isEqualTo(expectedValue);
    }
}
