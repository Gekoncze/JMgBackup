package cz.mg.backup.gui.common.enumerations;

import cz.mg.annotations.classes.Data;
import cz.mg.annotations.requirement.Mandatory;

import java.util.Enumeration;
import java.util.Iterator;

public @Data class IteratorEnumeration<T> implements Enumeration<T> {
    private final @Mandatory Iterator<T> iterator;

    public IteratorEnumeration(@Mandatory Iterator<T> iterator) {
        this.iterator = iterator;
    }

    @Override
    public boolean hasMoreElements() {
        return iterator.hasNext();
    }

    @Override
    public T nextElement() {
        return iterator.next();
    }
}
