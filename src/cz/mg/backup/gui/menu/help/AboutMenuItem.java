package cz.mg.backup.gui.menu.help;

import cz.mg.annotations.classes.Component;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.backup.gui.event.UserActionListener;
import cz.mg.backup.gui.MainWindow;
import cz.mg.backup.gui.dialogs.AboutDialog;

import javax.swing.*;
import java.awt.event.KeyEvent;

public @Component class AboutMenuItem extends JMenuItem {
    public AboutMenuItem(@Mandatory MainWindow window) {
        setText("About");
        setMnemonic('A');
        setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F1, 0));
        addActionListener(new UserActionListener(() -> AboutDialog.show(window)));
    }
}
