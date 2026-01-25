package cz.mg.backup.gui.views.directory;

import cz.mg.annotations.classes.Component;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.annotations.requirement.Optional;
import cz.mg.collections.list.List;

import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

public @Component class DirectoryTreeModel implements TreeModel {
    private final @Optional DirectoryTreeEntry root;
    private final @Mandatory List<TreeModelListener> listeners = new List<>();

    public DirectoryTreeModel(@Optional DirectoryTreeEntry root) {
        this.root = root;
    }

    @Override
    public @Optional Object getRoot() {
        return root;
    }

    @Override
    public @Mandatory Object getChild(@Mandatory Object object, int i) {
        return ((DirectoryTreeEntry) object).getChildren().get(i);
    }

    @Override
    public int getChildCount(@Mandatory Object object) {
        return ((DirectoryTreeEntry) object).getChildren().count();
    }

    @Override
    public boolean isLeaf(@Mandatory Object object) {
        return ((DirectoryTreeEntry) object).isLeaf();
    }

    @Override
    public void valueForPathChanged(@Mandatory TreePath treePath, @Mandatory Object object) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int getIndexOfChild(@Mandatory Object parent, @Mandatory Object child) {
        return ((DirectoryTreeEntry) child).getIndex();
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
