package cz.mg.backup.gui.views.directory.wrapper;

import cz.mg.annotations.classes.Component;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.backup.entities.Node;

import javax.swing.tree.TreeNode;
import java.util.Objects;

public abstract @Component class AbstractTreeNode implements TreeNode {
    public abstract @Mandatory Node getNode();

    @Override
    public String toString() {
        return getNode().getPath().getFileName() != null
            ? getNode().getPath().getFileName().toString()
            : "";
    }

    @Override
    public boolean equals(Object other) {
        return other instanceof AbstractTreeNode node && Objects.equals(getNode().getPath(), node.getNode().getPath());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getNode().getPath());
    }
}
