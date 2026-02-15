package cz.mg.backup.gui.menu;

import cz.mg.annotations.classes.Component;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.backup.gui.MainWindow;
import cz.mg.backup.gui.actions.menu.edit.SettingsAction;
import cz.mg.backup.gui.components.ActionItem;

import javax.swing.*;

public @Component class EditMenu extends JMenu {
    public EditMenu(@Mandatory MainWindow window) {
        setText("Edit");
        setMnemonic('E');
        add(new ActionItem(new SettingsAction(window)));
    }
}
