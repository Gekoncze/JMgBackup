package cz.mg.backup.gui.actions.directory;

import cz.mg.annotations.classes.Component;
import cz.mg.annotations.classes.Service;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.annotations.requirement.Optional;
import cz.mg.backup.gui.MainWindow;
import cz.mg.backup.gui.actions.Action;
import cz.mg.backup.gui.dialogs.ProgressDialog;
import cz.mg.backup.gui.entities.Side;
import cz.mg.backup.gui.entities.State;
import cz.mg.backup.gui.services.StateService;
import cz.mg.backup.gui.views.directory.DirectoryTreeView;
import cz.mg.backup.services.DirectoryManager;

import javax.swing.*;
import java.nio.file.Files;
import java.nio.file.Path;

public @Component class LoadAction implements Action {
    private final @Mandatory DirectoryManager directoryManager = DirectoryManager.getInstance();
    private final @Mandatory StateService stateService = StateService.getInstance();

    private final @Mandatory MainWindow window;
    private final @Mandatory DirectoryTreeView view;

    public LoadAction(@Mandatory MainWindow window, @Mandatory DirectoryTreeView view) {
        this.window = window;
        this.view = view;
    }

    @Override
    public @Mandatory String getName() {
        return "Load";
    }

    @Override
    public @Optional Character getMnemonic() {
        return 'L';
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
        Path path = view.getSelectionView().getPath();
        Side side = view.getSide();

        validate(path);

        ProgressDialog.run(
            window,
            getName(),
            progress -> {
                state.setDirectory(directoryManager.load(path, progress), side);
                stateService.refresh(state, progress);
            }
        );

        window.setApplicationState(state);
    }

    private void validate(@Optional Path path) {
        if (path != null) {
            if (!Files.exists(path)) {
                JOptionPane.showMessageDialog(
                    window,
                    "Selected directory does not exist.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE
                );
            } else if (!Files.isDirectory(path)) {
                JOptionPane.showMessageDialog(
                    window,
                    "Selected file is not a directory.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE
                );
            }
        }
    }
}
