package cz.mg.backup.gui.views.directory;

import cz.mg.annotations.classes.Component;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.annotations.requirement.Optional;
import cz.mg.collections.list.List;

import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

public @Component class ObjectTreeModel implements TreeModel {
    private final @Optional ObjectTreeEntry root;
    private final @Mandatory List<TreeModelListener> listeners = new List<>();

    public ObjectTreeModel(@Optional ObjectTreeEntry root) {
        this.root = root;
    }

    @Override
    public @Optional Object getRoot() {
        return root;
    }

    @Override
    public @Mandatory Object getChild(@Mandatory Object object, int i) {
        ObjectTreeEntry entry = (ObjectTreeEntry) object;
        if (entry.getChildren() != null) {
            return entry.getChildren().get(i);
        } else {
            throw new IllegalStateException();
        }
    }

    @Override
    public int getChildCount(@Mandatory Object object) {
        ObjectTreeEntry entry = (ObjectTreeEntry) object;
        if (entry.getChildren() != null) {
            return entry.getChildren().count();
        } else {
            return 0;
        }
    }

    @Override
    public boolean isLeaf(@Mandatory Object object) {
        ObjectTreeEntry entry = (ObjectTreeEntry) object;
        return entry.isLeaf();
    }

    @Override
    public void valueForPathChanged(@Mandatory TreePath treePath, @Mandatory Object object) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int getIndexOfChild(@Mandatory Object object, @Mandatory Object childObject) {
        ObjectTreeEntry childEntry = (ObjectTreeEntry) childObject;
        return childEntry.getIndex();
    }

    @Override
    public void addTreeModelListener(@Mandatory TreeModelListener listener) {
        listeners.addLast(listener);
    }

    @Override
    public void removeTreeModelListener(@Mandatory TreeModelListener listener) {
        listeners.removeIf(current -> current == listener);
    }
}
