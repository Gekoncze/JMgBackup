package cz.mg.backup.gui.views.directory;

import cz.mg.annotations.classes.Component;
import cz.mg.annotations.requirement.Optional;
import cz.mg.backup.entities.Directory;
import cz.mg.backup.gui.views.directory.wrapper.DirectoryTreeNode;
import cz.mg.collections.list.List;

import javax.swing.*;
import javax.swing.tree.TreePath;

@Component class DirectoryTree extends JTree {
    public DirectoryTree() {
        setBorder(BorderFactory.createEtchedBorder());
        setRoot(null);
        setCellRenderer(new DirectoryTreeCellRenderer());
    }

    public @Optional Directory getRoot() {
        DirectoryTreeNode root = ((DirectoryTreeModel) getModel()).getRoot();
        return root != null ? root.getDirectory() : null;
    }

    public void setRoot(@Optional Directory root) {
        List<TreePath> expandedPaths = TreeUtils.getExpandedPaths(this);
        TreePath collapsedRootPath = TreeUtils.getCollapsedRootPath(this);
        List<TreePath> selectedPaths = TreeUtils.getSelectedPaths(this);

        setModel(new DirectoryTreeModel(root));

        TreeUtils.expandPaths(this, expandedPaths);
        TreeUtils.collapseRootPath(this, collapsedRootPath);
        TreeUtils.selectPaths(this, selectedPaths);
    }
}
