package cz.mg.backup.gui.components.menu.edit;

import cz.mg.annotations.requirement.Mandatory;
import cz.mg.backup.gui.event.UserActionListener;
import cz.mg.backup.gui.MainWindow;
import cz.mg.backup.gui.components.dialog.HashFunctionDialog;

import javax.swing.*;

public class HashAlgorithmMenuItem extends JMenuItem {
    public HashAlgorithmMenuItem(@Mandatory MainWindow window) {
        setText("Hash algorithm");
        setMnemonic('H');
        addActionListener(new UserActionListener(() -> HashFunctionDialog.show(window)));
    }
}
