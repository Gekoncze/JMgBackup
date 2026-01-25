package cz.mg.backup.test.predicates;

import cz.mg.functions.Predicate;

public record Range(long min, long max) implements Predicate<Long> {
    @Override
    public boolean match(Long l) {
        return l >= min && l <= max;
    }
}
