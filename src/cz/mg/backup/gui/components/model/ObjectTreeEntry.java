package cz.mg.backup.gui.components.model;

import cz.mg.annotations.classes.Component;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.annotations.requirement.Optional;
import cz.mg.collections.array.Array;

public @Component class ObjectTreeEntry {
    private final @Mandatory Object object;
    private final @Mandatory String name;
    private final int index;
    private final boolean isLeaf;
    private final @Optional Array<ObjectTreeEntry> children;

    public ObjectTreeEntry(
        @Mandatory Object object,
        @Mandatory String name,
        int index,
        boolean isLeaf,
        @Optional Array<ObjectTreeEntry> children
    ) {
        this.object = object;
        this.name = name;
        this.index = index;
        this.isLeaf = isLeaf;
        this.children = children;
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
}
