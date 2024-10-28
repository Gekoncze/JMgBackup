package cz.mg.backup.gui.menu.file;

import cz.mg.annotations.classes.Component;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.backup.gui.event.UserActionListener;
import cz.mg.backup.gui.MainWindow;

import javax.swing.*;

public @Component class ExitMenuItem extends JMenuItem {
    public ExitMenuItem(@Mandatory MainWindow window) {
        setText("Exit");
        setMnemonic('E');
        addActionListener(new UserActionListener(window::dispose));
    }
}
