package cz.mg.backup.gui.views.directory;

import cz.mg.annotations.classes.Component;
import cz.mg.annotations.requirement.Optional;

import javax.swing.*;

@Component class DirectoryTree extends JTree {
    public DirectoryTree() {
        setBorder(BorderFactory.createEtchedBorder());
        setRoot(null);
        setCellRenderer(new DirectoryTreeCellRenderer());
    }

    public @Optional DirectoryTreeEntry getRoot() {
        return ((DirectoryTreeModel) getModel()).getRoot();
    }

    public void setRoot(@Optional DirectoryTreeEntry root) {
        setModel(new DirectoryTreeModel(root));
    }
}
