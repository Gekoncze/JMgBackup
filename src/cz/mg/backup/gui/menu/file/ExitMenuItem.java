package cz.mg.backup.gui.menu.file;

import cz.mg.annotations.classes.Component;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.backup.gui.event.UserActionListener;
import cz.mg.backup.gui.MainWindow;
import cz.mg.backup.gui.icons.Icons;

import javax.swing.*;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

public @Component class ExitMenuItem extends JMenuItem {
    public ExitMenuItem(@Mandatory MainWindow window) {
        setIcon(Icons.STANDARD_EXIT_16);
        setText("Exit");
        setMnemonic('E');
        setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F4, InputEvent.ALT_DOWN_MASK));
        addActionListener(new UserActionListener(window::dispose));
    }
}
