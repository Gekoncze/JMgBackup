package cz.mg.backup.gui.actions.menu.edit;

import cz.mg.annotations.classes.Component;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.annotations.requirement.Optional;
import cz.mg.backup.gui.MainWindow;
import cz.mg.backup.gui.actions.Action;
import cz.mg.backup.gui.dialogs.SettingsDialog;
import cz.mg.backup.gui.icons.Icons;

import javax.swing.*;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

public @Component class SettingsAction implements Action {
    private final @Mandatory MainWindow window;

    public SettingsAction(@Mandatory MainWindow window) {
        this.window = window;
    }

    @Override
    public @Mandatory String getName() {
        return "Settings";
    }

    @Override
    public @Optional Character getMnemonic() {
        return 'S';
    }

    @Override
    public @Optional KeyStroke getShortcut() {
        return KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_DOWN_MASK | InputEvent.SHIFT_DOWN_MASK);
    }

    @Override
    public @Optional Icon getSmallIcon() {
        return Icons.STANDARD_SETTINGS_16;
    }

    @Override
    public @Optional Icon getLargeIcon() {
        return Icons.STANDARD_SETTINGS_20;
    }

    @Override
    public void run() {
        SettingsDialog.show(window);
    }
}
