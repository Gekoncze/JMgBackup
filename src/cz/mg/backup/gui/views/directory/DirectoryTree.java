package cz.mg.backup.gui.views.directory;

import cz.mg.annotations.classes.Component;

import javax.swing.*;

public @Component class DirectoryTree extends JTree {
    public DirectoryTree() {
        setBorder(BorderFactory.createEtchedBorder());
        setModel(new DirectoryTreeModel(null));
    }
}
