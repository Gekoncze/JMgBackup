package cz.mg.backup.gui.menu;

import cz.mg.annotations.classes.Component;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.backup.event.UserActionListener;
import cz.mg.backup.gui.MainWindow;

import javax.swing.*;

public @Component class ExitMenuItem extends JMenuItem {
    private final @Mandatory MainWindow window;

    public ExitMenuItem(@Mandatory MainWindow window) {
        this.window = window;
        setText("Exit");
        setMnemonic('E');
        addActionListener(new UserActionListener(this::exit));
    }

    private void exit() {
        window.dispose();
    }
}
