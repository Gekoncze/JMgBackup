package cz.mg.backup.gui.menu;

import cz.mg.annotations.classes.Component;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.backup.gui.MainWindow;
import cz.mg.backup.gui.menu.edit.EditMenu;
import cz.mg.backup.gui.menu.file.FileMenu;
import cz.mg.backup.gui.menu.help.HelpMenu;

import javax.swing.*;

public @Component class MainMenuBar extends JMenuBar {
    public MainMenuBar(@Mandatory MainWindow window) {
        add(new FileMenu(window));
        add(new EditMenu(window));
        add(new HelpMenu(window));
    }
}
