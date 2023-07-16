package cz.mg.backup.gui.menu;

import cz.mg.annotations.classes.Component;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.backup.gui.MainWindow;

import javax.swing.*;

public @Component class HelpMenu extends JMenu {
    public HelpMenu(@Mandatory MainWindow window) {
        setText("Help");
        setMnemonic('H');
        add(new AboutMenuItem(window));
    }
}
