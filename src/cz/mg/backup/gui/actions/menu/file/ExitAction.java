package cz.mg.backup.gui.actions.menu.file;

import cz.mg.annotations.classes.Component;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.annotations.requirement.Optional;
import cz.mg.backup.gui.MainWindow;
import cz.mg.backup.gui.actions.Action;
import cz.mg.backup.gui.icons.Icons;

import javax.swing.*;
import java.awt.event.KeyEvent;

public @Component class ExitAction implements Action {
    private final @Mandatory MainWindow window;

    public ExitAction(@Mandatory MainWindow window) {
        this.window = window;
    }

    @Override
    public @Mandatory String getName() {
        return "Exit";
    }

    @Override
    public @Optional Character getMnemonic() {
        return 'E';
    }

    @Override
    public @Optional KeyStroke getShortcut() {
        return KeyStroke.getKeyStroke(KeyEvent.VK_F4, KeyEvent.ALT_DOWN_MASK);
    }

    @Override
    public @Optional Icon getSmallIcon() {
        return Icons.STANDARD_EXIT_16;
    }

    @Override
    public @Optional Icon getLargeIcon() {
        return Icons.STANDARD_EXIT_20;
    }

    @Override
    public void run() {
        window.dispose();
    }
}
