package cz.mg.backup.gui.actions.directory.menu;

import cz.mg.annotations.classes.Component;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.annotations.requirement.Optional;
import cz.mg.backup.entities.Node;
import cz.mg.backup.gui.MainWindow;
import cz.mg.backup.gui.actions.Action;
import cz.mg.backup.gui.dialogs.ProgressDialog;
import cz.mg.backup.gui.entities.State;
import cz.mg.backup.gui.services.RefreshService;
import cz.mg.backup.gui.views.directory.DirectoryTreeView;
import cz.mg.backup.services.ChecksumManager;
import cz.mg.collections.list.List;

import javax.swing.*;

public @Component class ClearChecksumAction implements Action {
    private final @Mandatory ChecksumManager checksumManager = ChecksumManager.getInstance();
    private final @Mandatory RefreshService refreshService = RefreshService.getInstance();

    private final @Mandatory MainWindow window;
    private final @Mandatory DirectoryTreeView view;

    public ClearChecksumAction(@Mandatory MainWindow window, @Mandatory DirectoryTreeView view) {
        this.window = window;
        this.view = view;
    }

    @Override
    public @Mandatory String getName() {
        return "Clear checksum";
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
        State state = window.getApplicationState();
        List<Node> nodes = view.getSelectedNodes();

        ProgressDialog.run(
            window,
            getName(),
            progress -> {
                checksumManager.clear(nodes, progress);
                refreshService.refresh(state, progress);
            }
        );

        window.setApplicationState(state);
    }
}
