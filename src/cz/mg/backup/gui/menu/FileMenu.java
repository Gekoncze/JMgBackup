package cz.mg.backup.gui.menu;

import cz.mg.annotations.classes.Component;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.backup.gui.MainWindow;

import javax.swing.*;

public @Component class FileMenu extends JMenu {
    public FileMenu(@Mandatory MainWindow window) {
        setText("File");
        setMnemonic('F');
        add(new ExitMenuItem(window));
    }
}
