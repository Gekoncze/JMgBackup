package cz.mg.backup.gui.components.menu.help;

import cz.mg.annotations.classes.Component;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.backup.gui.event.UserActionListener;
import cz.mg.backup.gui.MainWindow;
import cz.mg.backup.gui.components.dialog.AboutDialog;

import javax.swing.*;

public @Component class AboutMenuItem extends JMenuItem {
    public AboutMenuItem(@Mandatory MainWindow window) {
        setText("About");
        setMnemonic('A');
        addActionListener(new UserActionListener(() -> AboutDialog.show(window)));
    }
}
