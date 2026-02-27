package cz.mg.backup.gui.actions.directory.menu;

import cz.mg.annotations.classes.Component;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.annotations.requirement.Optional;
import cz.mg.backup.entities.Algorithm;
import cz.mg.backup.entities.Node;
import cz.mg.backup.gui.MainWindow;
import cz.mg.backup.gui.actions.Action;
import cz.mg.backup.gui.dialogs.ProgressDialog;
import cz.mg.backup.gui.entities.State;
import cz.mg.backup.gui.services.StateService;
import cz.mg.backup.gui.views.directory.DirectoryTreeView;
import cz.mg.backup.services.FileBackup;
import cz.mg.collections.list.List;

import javax.swing.*;

public @Component class CopyMissingFilesAction implements Action {
    private final @Mandatory FileBackup fileBackup = FileBackup.getInstance();
    private final @Mandatory StateService stateService = StateService.getInstance();

    private final @Mandatory MainWindow window;
    private final @Mandatory DirectoryTreeView source;

    public CopyMissingFilesAction(@Mandatory MainWindow window, @Mandatory DirectoryTreeView source) {
        this.window = window;
        this.source = source;
    }

    @Override
    public @Mandatory String getName() {
        return "Copy missing files";
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
        List<Node> nodes = source.getSelectedNodes();
        Algorithm algorithm = window.getSettings().getAlgorithm();
        DirectoryTreeView target = window.getLeftView() == source ? window.getRightView() : window.getLeftView();

        if (source.getRoot() != null && target.getRoot() != null) {
            ProgressDialog.run(
                window,
                getName(),
                progress -> {
                    fileBackup.copyMissingFiles(nodes, source.getRoot(), target.getRoot(), algorithm, progress);
                    stateService.refresh(state, progress);
                }
            );

            window.setApplicationState(state);
        }
    }
}
