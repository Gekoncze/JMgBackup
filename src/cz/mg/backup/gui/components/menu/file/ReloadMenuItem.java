package cz.mg.backup.gui.components.menu.file;

import cz.mg.annotations.classes.Component;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.backup.gui.MainWindow;
import cz.mg.backup.gui.event.UserActionListener;

import javax.swing.*;
import java.awt.event.KeyEvent;

public @Component class ReloadMenuItem extends JMenuItem {
    public ReloadMenuItem(@Mandatory MainWindow window) {
        setText("Reload");
        setMnemonic('R');
        setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F5, 0));
        addActionListener(new UserActionListener(window::reload));
    }
}
