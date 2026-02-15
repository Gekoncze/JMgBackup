package cz.mg.backup.gui.views.directory.wrapper;

import cz.mg.annotations.classes.Component;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.annotations.requirement.Optional;
import cz.mg.backup.entities.File;
import cz.mg.backup.entities.Node;
import cz.mg.backup.gui.common.enumerations.EmptyEnumeration;

import javax.swing.tree.TreeNode;
import java.util.Enumeration;
import java.util.Objects;

public @Component class FileTreeNode extends AbstractTreeNode {
    private final @Optional TreeNode parent;
    private final @Optional Integer index;
    private final @Mandatory File file;

    public FileTreeNode(@Optional TreeNode parent, @Optional Integer index, @Mandatory File file) {
        this.parent = parent;
        this.index = index;
        this.file = file;
    }

    @Override
    public @Mandatory Node getNode() {
        return file;
    }

    public @Mandatory File getFile() {
        return file;
    }

    @Override
    public @Mandatory TreeNode getChildAt(int i) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int getChildCount() {
        return 0;
    }

    @Override
    public @Optional TreeNode getParent() {
        return parent;
    }

    @Override
    public int getIndex(@Optional TreeNode parent) {
        return parent != null && index != null && parent == this.parent
            ? Objects.requireNonNull(index)
            : -1;
    }

    @Override
    public boolean getAllowsChildren() {
        return false;
    }

    @Override
    public boolean isLeaf() {
        return true;
    }

    @Override
    public @Mandatory Enumeration<? extends TreeNode> children() {
        return new EmptyEnumeration<>();
    }
}
