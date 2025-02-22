package cz.mg.backup.gui.menu.file;

import cz.mg.annotations.classes.Component;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.backup.gui.MainWindow;
import cz.mg.backup.gui.event.UserActionListener;
import cz.mg.backup.gui.icons.Icons;

import javax.swing.*;
import java.awt.event.KeyEvent;

public @Component class ReloadMenuItem extends JMenuItem {
    public ReloadMenuItem(@Mandatory MainWindow window) {
        setIcon(Icons.STANDARD_RELOAD_16);
        setText("Reload");
        setMnemonic('R');
        setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F5, 0));
        addActionListener(new UserActionListener(window::reload));
    }
}
