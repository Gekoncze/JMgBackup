package cz.mg.backup.test.predicates;

import cz.mg.functions.Predicate;

public record Greater(long limit) implements Predicate<Long> {
    @Override
    public boolean match(Long reality) {
        return reality > limit;
    }
}
