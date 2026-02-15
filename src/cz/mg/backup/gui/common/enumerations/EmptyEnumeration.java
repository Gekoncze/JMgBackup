package cz.mg.backup.gui.common.enumerations;

import cz.mg.annotations.classes.Data;

import java.util.Enumeration;

public @Data class EmptyEnumeration<T> implements Enumeration<T> {
    @Override
    public boolean hasMoreElements() {
        return false;
    }

    @Override
    public T nextElement() {
        throw new UnsupportedOperationException();
    }
}
