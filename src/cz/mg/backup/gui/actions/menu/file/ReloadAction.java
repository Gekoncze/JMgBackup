package cz.mg.backup.gui.actions.menu.file;

import cz.mg.annotations.classes.Component;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.annotations.requirement.Optional;
import cz.mg.backup.gui.MainWindow;
import cz.mg.backup.gui.actions.Action;
import cz.mg.backup.gui.dialogs.ProgressDialog;
import cz.mg.backup.gui.entities.State;
import cz.mg.backup.gui.icons.Icons;
import cz.mg.backup.gui.services.RefreshService;
import cz.mg.backup.services.DirectoryManager;

import javax.swing.*;
import java.awt.event.KeyEvent;

public @Component class ReloadAction implements Action {
    private final @Mandatory DirectoryManager directoryManager = DirectoryManager.getInstance();
    private final @Mandatory RefreshService refreshService = RefreshService.getInstance();

    private final @Mandatory MainWindow window;

    public ReloadAction(@Mandatory MainWindow window) {
        this.window = window;
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
        return KeyStroke.getKeyStroke(KeyEvent.VK_F5, 0);
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

        // process can be cancelled at any time, so keep that in mind to ensure some level of consistency
        ProgressDialog.run(
            window,
            getName(),
            progress -> {
                directoryManager.reload(state.getLeft(), progress);
                directoryManager.reload(state.getRight(), progress);
                refreshService.refresh(state, progress);
            }
        );

        window.setApplicationState(state);
    }
}
