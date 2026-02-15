package cz.mg.backup.gui.actions.menu.help;

import cz.mg.annotations.classes.Component;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.annotations.requirement.Optional;
import cz.mg.backup.gui.MainWindow;
import cz.mg.backup.gui.actions.Action;
import cz.mg.backup.gui.dialogs.AboutDialog;
import cz.mg.backup.gui.icons.Icons;

import javax.swing.*;
import java.awt.event.KeyEvent;

public @Component class AboutAction implements Action {
    private final @Mandatory MainWindow window;

    public AboutAction(@Mandatory MainWindow window) {
        this.window = window;
    }

    @Override
    public @Mandatory String getName() {
        return "About";
    }

    @Override
    public @Optional Character getMnemonic() {
        return 'A';
    }

    @Override
    public @Optional KeyStroke getShortcut() {
        return KeyStroke.getKeyStroke(KeyEvent.VK_F1, 0);
    }

    @Override
    public @Optional Icon getSmallIcon() {
        return Icons.STANDARD_HELP_16;
    }

    @Override
    public @Optional Icon getLargeIcon() {
        return Icons.STANDARD_HELP_20;
    }

    @Override
    public void run() {
        AboutDialog.show(window);
    }
}
