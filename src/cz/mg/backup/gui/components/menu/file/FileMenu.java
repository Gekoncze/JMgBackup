package cz.mg.backup.gui.components.menu.file;

import cz.mg.annotations.classes.Component;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.backup.gui.MainWindow;

import javax.swing.*;

public @Component class FileMenu extends JMenu {
    public FileMenu(@Mandatory MainWindow window) {
        setText("File");
        setMnemonic('F');
        add(new ReloadMenuItem(window));
        add(new ExitMenuItem(window));
    }
}
