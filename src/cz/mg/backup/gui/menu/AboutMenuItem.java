package cz.mg.backup.gui.menu;

import cz.mg.annotations.classes.Component;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.backup.event.UserActionListener;
import cz.mg.backup.gui.MainWindow;
import cz.mg.backup.gui.dialog.AboutDialog;

import javax.swing.*;

public @Component class AboutMenuItem extends JMenuItem {
    private final @Mandatory MainWindow window;

    public AboutMenuItem(@Mandatory MainWindow window) {
        this.window = window;
        setText("About");
        setMnemonic('A');
        addActionListener(new UserActionListener(this::showDialog));
    }

    private void showDialog() {
        new AboutDialog(window).setVisible(true);
    }
}
