package cz.mg.backup.gui.actions.directory;

import cz.mg.annotations.classes.Component;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.annotations.requirement.Optional;
import cz.mg.backup.components.Status;
import cz.mg.backup.components.Task;
import cz.mg.backup.entities.Directory;
import cz.mg.backup.gui.MainWindow;
import cz.mg.backup.gui.actions.Action;
import cz.mg.backup.gui.dialogs.ProgressDialog;
import cz.mg.backup.gui.entities.State;
import cz.mg.backup.gui.icons.Icons;
import cz.mg.backup.gui.services.RefreshService;
import cz.mg.backup.gui.views.directory.DirectoryTreeView;
import cz.mg.backup.services.DirectoryManager;

import javax.swing.*;
import java.awt.event.KeyEvent;

public @Component class ReloadAction implements Action {
    private final @Mandatory DirectoryManager directoryManager = DirectoryManager.getInstance();
    private final @Mandatory RefreshService refreshService = RefreshService.getInstance();

    private final @Mandatory MainWindow window;
    private final @Mandatory DirectoryTreeView view;

    public ReloadAction(@Mandatory MainWindow window, @Mandatory DirectoryTreeView view) {
        this.window = window;
        this.view = view;
    }

    @Override
    public @Mandatory String getName() {
        return "Reload";
    }

    @Override
    public @Optional Character getMnemonic() {
        return 'R';
    }

    @Override
    public @Optional KeyStroke getShortcut() {
        return KeyStroke.getKeyStroke(KeyEvent.VK_F5, KeyEvent.SHIFT_DOWN_MASK);
    }

    @Override
    public @Optional Icon getSmallIcon() {
        return Icons.STANDARD_RELOAD_16;
    }

    @Override
    public @Optional Icon getLargeIcon() {
        return Icons.STANDARD_RELOAD_20;
    }

    @Override
    public void run() {
        State state = window.getApplicationState();
        Directory root = view.getRoot();

        Task<?> task = ProgressDialog.run(
            window,
            getName(),
            progress -> {
                directoryManager.reload(root, progress);
                refreshService.refresh(state, progress);
            }
        );

        window.setApplicationState(state, task.getStatus());

        if (task.getStatus() == Status.COMPLETED) {
            view.getBanner().close();
        }
    }
}
