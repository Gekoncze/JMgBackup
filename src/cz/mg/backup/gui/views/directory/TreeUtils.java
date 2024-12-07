package cz.mg.backup.gui.views.directory;

import cz.mg.annotations.classes.Static;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.annotations.requirement.Optional;
import cz.mg.collections.list.List;

import javax.swing.*;
import javax.swing.tree.TreePath;
import java.util.Enumeration;

public @Static class TreeUtils {
    public static @Mandatory List<TreePath> getExpandedPaths(@Mandatory JTree tree) {
        return convert(tree.getExpandedDescendants(getRootPath(tree)));
    }

    public static @Optional TreePath getCollapsedRootPath(@Mandatory JTree tree) {
        TreePath root = getRootPath(tree);
        if (root != null && !tree.isExpanded(root)) {
            return root;
        }
        return null;
    }

    public static @Mandatory List<TreePath> getSelectedPaths(@Mandatory JTree tree) {
        return convert(tree.getSelectionPaths());
    }

    public static @Optional TreePath getRootPath(@Mandatory JTree tree) {
        Object root = tree.getModel().getRoot();
        return root == null ? null : new TreePath(root);
    }

    private static @Mandatory List<TreePath> convert(@Optional Enumeration<TreePath> pathEnumeration) {
        List<TreePath> pathList = new List<>();
        if (pathEnumeration != null) {
            while (pathEnumeration.hasMoreElements()) {
                pathList.addLast(pathEnumeration.nextElement());
            }
        }
        return pathList;
    }

    private static @Mandatory List<TreePath> convert(@Optional TreePath[] pathArray) {
        List<TreePath> pathList = new List<>();
        if (pathArray != null) {
            for (TreePath path : pathArray) {
                pathList.addLast(path);
            }
        }
        return pathList;
    }

    public static void expandPaths(@Mandatory JTree tree, @Mandatory List<TreePath> expandedPaths) {
        for (TreePath expandedPath : expandedPaths) {
            tree.expandPath(expandedPath);
        }
    }

    public static void collapseRootPath(@Mandatory JTree tree, @Optional TreePath collapsedRoot) {
        if (collapsedRoot != null) {
            tree.collapsePath(collapsedRoot);
        }
    }

    public static void selectPaths(@Mandatory JTree tree, @Mandatory List<TreePath> selectedPaths) {
        for (TreePath selectedPath : selectedPaths) {
            tree.addSelectionPath(selectedPath);
        }
    }
}
