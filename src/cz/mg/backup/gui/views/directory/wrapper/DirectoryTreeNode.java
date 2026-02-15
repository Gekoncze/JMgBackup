package cz.mg.backup.gui.views.directory.wrapper;

import cz.mg.annotations.classes.Component;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.annotations.requirement.Optional;
import cz.mg.backup.entities.Directory;
import cz.mg.backup.entities.File;
import cz.mg.backup.entities.Node;
import cz.mg.backup.gui.common.enumerations.IteratorEnumeration;
import cz.mg.collections.array.Array;

import javax.swing.tree.TreeNode;
import java.util.Enumeration;
import java.util.Objects;

public @Component class DirectoryTreeNode extends AbstractTreeNode {
    private final @Optional TreeNode parent;
    private final @Optional Integer index;
    private final @Mandatory Directory directory;
    private @Optional Array<TreeNode> children;

    public DirectoryTreeNode(@Optional TreeNode parent, @Optional Integer index, @Mandatory Directory directory) {
        this.parent = parent;
        this.index = index;
        this.directory = directory;
    }

    @Override
    public @Mandatory Node getNode() {
        return directory;
    }

    public @Mandatory Directory getDirectory() {
        return directory;
    }

    @Override
    public TreeNode getChildAt(int i) {
        return getChildren().get(i);
    }

    @Override
    public int getChildCount() {
        return directory.getDirectories().count() + directory.getFiles().count();
    }

    @Override
    public @Optional TreeNode getParent() {
        return parent;
    }

    @Override
    public int getIndex(TreeNode parent) {
        return parent != null && index != null && parent == this.parent
            ? Objects.requireNonNull(index)
            : -1;
    }

    @Override
    public boolean getAllowsChildren() {
        return true;
    }

    @Override
    public boolean isLeaf() {
        return false;
    }

    @Override
    public Enumeration<? extends TreeNode> children() {
        return new IteratorEnumeration<>(getChildren().iterator());
    }

    private @Mandatory Array<TreeNode> getChildren() {
        if (children == null) {
            children = new Array<>(directory.getDirectories().count() + directory.getFiles().count());
            int i = 0;

            for (Directory subDirectory : directory.getDirectories()) {
                children.set(i, new DirectoryTreeNode(this, i, subDirectory));
                i++;
            }

            for (File file : directory.getFiles()) {
                children.set(i, new FileTreeNode(this, i, file));
                i++;
            }
        }
        return children;
    }
}
