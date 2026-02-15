package cz.mg.backup.gui.actions.directory.menu;

import cz.mg.annotations.classes.Component;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.annotations.requirement.Optional;
import cz.mg.backup.entities.Node;
import cz.mg.backup.gui.actions.Action;
import cz.mg.backup.gui.services.Platform;
import cz.mg.backup.gui.views.directory.DirectoryTreeView;

import javax.swing.*;

public @Component class OpenFileManagerAction implements Action {
    private final @Mandatory Platform platform = Platform.getInstance();

    private final @Mandatory DirectoryTreeView view;

    public OpenFileManagerAction(@Mandatory DirectoryTreeView view) {
        this.view = view;
    }

    @Override
    public @Mandatory String getName() {
        return "Open file manager";
    }

    @Override
    public @Optional Character getMnemonic() {
        return null;
    }

    @Override
    public @Optional KeyStroke getShortcut() {
        return null;
    }

    @Override
    public @Optional Icon getSmallIcon() {
        return null;
    }

    @Override
    public @Optional Icon getLargeIcon() {
        return null;
    }

    @Override
    public void run() {
        Node node = view.getPopupNode();
        if (node != null) {
            platform.openFileManager(node.getPath());
        }
    }
}
