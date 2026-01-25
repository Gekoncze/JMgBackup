package cz.mg.backup.test.predicates;

import cz.mg.functions.Predicate;

import java.util.Objects;

public record Equals(long expectation) implements Predicate<Long> {
    @Override
    public boolean match(Long reality) {
        return Objects.equals(expectation, reality);
    }
}
