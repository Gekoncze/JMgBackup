package cz.mg.backup.gui.views.directory;

import cz.mg.annotations.classes.Component;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.annotations.requirement.Optional;
import cz.mg.collections.array.Array;
import cz.mg.functions.EqualsFunction;
import cz.mg.functions.HashFunction;

import java.util.Objects;

@SuppressWarnings({"rawtypes", "unchecked"})
public @Component class ObjectTreeEntry {
    private final @Mandatory Object object;
    private final @Mandatory String name;
    private final int index;
    private final boolean isLeaf;
    private final @Optional Array<ObjectTreeEntry> children;
    private final @Mandatory EqualsFunction equalsFunction;
    private final @Mandatory HashFunction hashFunction;

    public <T> ObjectTreeEntry(
        @Mandatory T object,
        @Mandatory String name,
        int index,
        boolean isLeaf,
        @Optional Array<ObjectTreeEntry> children,
        @Mandatory EqualsFunction<T> equalsFunction,
        @Mandatory HashFunction<T> HashFunction
    ) {
        this.object = object;
        this.name = name;
        this.index = index;
        this.isLeaf = isLeaf;
        this.children = children;
        this.equalsFunction = equalsFunction;
        this.hashFunction = HashFunction;
    }

    public @Mandatory Object get() {
        return object;
    }

    public int getIndex() {
        return index;
    }

    public boolean isLeaf() {
        return isLeaf;
    }

    public @Optional Array<ObjectTreeEntry> getChildren() {
        return children;
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof ObjectTreeEntry e) {
            if (Objects.equals(getClass(object), getClass(e.object))) {
                return equalsFunction.equals(object, e.object);
            }
        }
        return false;
    }

    @Override
    public int hashCode() {
        return hashFunction.hash(object);
    }

    private @Optional Class getClass(@Optional Object o) {
        return o == null ? null : o.getClass();
    }
}
