package cz.mg.backup.gui.views.directory;

import cz.mg.annotations.classes.Component;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.annotations.requirement.Optional;
import cz.mg.backup.entities.Node;
import cz.mg.collections.array.Array;
import cz.mg.functions.EqualsFunction;
import cz.mg.functions.HashFunction;

import java.util.Objects;

@SuppressWarnings({"rawtypes", "unchecked"})
public @Component class DirectoryTreeEntry {
    private final @Mandatory Node node;
    private final @Mandatory String name;
    private final int index;
    private final boolean isLeaf;
    private final @Mandatory Array<DirectoryTreeEntry> children;
    private final @Mandatory EqualsFunction equalsFunction;
    private final @Mandatory HashFunction hashFunction;

    public <T extends Node> DirectoryTreeEntry(
        @Mandatory T node,
        @Mandatory String name,
        int index,
        boolean isLeaf,
        @Mandatory Array<DirectoryTreeEntry> children,
        @Mandatory EqualsFunction<T> equalsFunction,
        @Mandatory HashFunction<T> HashFunction
    ) {
        this.node = node;
        this.name = name;
        this.index = index;
        this.isLeaf = isLeaf;
        this.children = children;
        this.equalsFunction = equalsFunction;
        this.hashFunction = HashFunction;
    }

    public @Mandatory Node get() {
        return node;
    }

    public int getIndex() {
        return index;
    }

    public boolean isLeaf() {
        return isLeaf;
    }

    public @Mandatory Array<DirectoryTreeEntry> getChildren() {
        return children;
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public boolean equals(@Optional Object object) {
        if (object instanceof DirectoryTreeEntry entry) {
            if (Objects.equals(node.getClass(), entry.node.getClass())) {
                return equalsFunction.equals(node, entry.node);
            }
        }
        return false;
    }

    @Override
    public int hashCode() {
        return hashFunction.hash(node);
    }
}
