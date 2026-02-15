package cz.mg.backup.gui.views.directory;

import cz.mg.annotations.classes.Component;
import cz.mg.annotations.requirement.Optional;
import cz.mg.backup.entities.Directory;
import cz.mg.backup.gui.views.directory.wrapper.DirectoryTreeNode;

import javax.swing.tree.DefaultTreeModel;

@Component class DirectoryTreeModel extends DefaultTreeModel {
    public DirectoryTreeModel(@Optional Directory root) {
        super(root != null ? new DirectoryTreeNode(null, null, root) : null);
    }

    @Override
    public @Optional DirectoryTreeNode getRoot() {
        return (DirectoryTreeNode) super.getRoot();
    }
}
