package cz.mg.backup.gui.menu;

import cz.mg.annotations.classes.Component;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.backup.gui.MainWindow;
import cz.mg.backup.gui.actions.menu.file.ExitAction;
import cz.mg.backup.gui.actions.menu.file.ReloadAction;
import cz.mg.backup.gui.components.ActionItem;

import javax.swing.*;

public @Component class FileMenu extends JMenu {
    public FileMenu(@Mandatory MainWindow window) {
        setText("File");
        setMnemonic('F');
        add(new ActionItem(new ReloadAction(window)));
        add(new ActionItem(new ExitAction(window)));
    }
}
