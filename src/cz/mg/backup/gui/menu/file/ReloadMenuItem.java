package cz.mg.backup.gui.menu.file;

import cz.mg.annotations.classes.Component;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.backup.gui.MainWindow;
import cz.mg.backup.gui.event.UserActionListener;
import cz.mg.backup.gui.icons.Icons;
import cz.mg.backup.gui.services.DirectoryTreeActions;

import javax.swing.*;
import java.awt.event.KeyEvent;

public @Component class ReloadMenuItem extends JMenuItem {
    private final @Mandatory DirectoryTreeActions actions = DirectoryTreeActions.getInstance();

    public ReloadMenuItem(@Mandatory MainWindow window) {
        setIcon(Icons.STANDARD_RELOAD_16);
        setText("Reload");
        setMnemonic('R');
        setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F5, 0));
        addActionListener(new UserActionListener(() -> actions.reload(window)));
    }
}
