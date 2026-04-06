package cz.mg.backup.services.matcher;

import cz.mg.annotations.requirement.Mandatory;
import cz.mg.functions.EqualsFunction;
import cz.mg.functions.HashFunction;

import java.util.Objects;

public class KeyComparator implements EqualsFunction<Key>, HashFunction<Key> {
    @Override
    public boolean equals(@Mandatory Key a, @Mandatory Key b) {
        return Objects.equals(a.getName(), b.getName())
            && Objects.equals(a.getSize(), b.getSize())
            && Objects.equals(a.getAlgorithm(), b.getAlgorithm())
            && Objects.equals(a.getHash(), b.getHash());
    }

    @Override
    public int hash(@Mandatory Key key) {
        return Objects.hash(
            key.getName(),
            key.getSize(),
            key.getAlgorithm(),
            key.getHash()
        );
    }
}
