package cz.mg.backup.gui.components.menu.edit;

import cz.mg.annotations.classes.Component;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.backup.gui.MainWindow;

import javax.swing.*;

public @Component class EditMenu extends JMenu {
    public EditMenu(@Mandatory MainWindow window) {
        setText("Edit");
        setMnemonic('E');
        add(new HashAlgorithmMenuItem(window));
    }
}
