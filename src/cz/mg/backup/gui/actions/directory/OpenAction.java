package cz.mg.backup.gui.actions.directory;

import cz.mg.annotations.classes.Component;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.annotations.requirement.Optional;
import cz.mg.backup.gui.MainWindow;
import cz.mg.backup.gui.actions.Action;
import cz.mg.backup.gui.dialogs.OpenDirectoryDialog;
import cz.mg.backup.gui.icons.Icons;
import cz.mg.backup.gui.views.directory.DirectoryTreeView;

import javax.swing.*;
import java.awt.event.KeyEvent;
import java.nio.file.Path;

public @Component class OpenAction implements Action {
    private final @Mandatory MainWindow window;
    private final @Mandatory DirectoryTreeView view;

    public OpenAction(@Mandatory MainWindow window, @Mandatory DirectoryTreeView view) {
        this.window = window;
        this.view = view;
    }

    @Override
    public @Mandatory String getName() {
        return "Open";
    }

    @Override
    public @Optional Character getMnemonic() {
        return 'O';
    }

    @Override
    public @Optional KeyStroke getShortcut() {
        return KeyStroke.getKeyStroke(KeyEvent.VK_O, KeyEvent.CTRL_DOWN_MASK);
    }

    @Override
    public @Optional Icon getSmallIcon() {
        return Icons.STANDARD_OPEN_16;
    }

    @Override
    public @Optional Icon getLargeIcon() {
        return Icons.STANDARD_OPEN_20;
    }

    @Override
    public void run() {
        Path path = OpenDirectoryDialog.show(window);
        if (path != null) {
            view.getSelectionView().setPath(path);
        }
    }
}
