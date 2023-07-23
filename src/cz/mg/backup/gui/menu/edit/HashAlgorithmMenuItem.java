package cz.mg.backup.gui.menu.edit;

import cz.mg.annotations.requirement.Mandatory;
import cz.mg.backup.event.UserActionListener;
import cz.mg.backup.gui.MainWindow;
import cz.mg.backup.gui.dialog.HashFunctionDialog;

import javax.swing.*;

public class HashAlgorithmMenuItem extends JMenuItem {
    private final @Mandatory MainWindow window;

    public HashAlgorithmMenuItem(@Mandatory MainWindow window) {
        this.window = window;
        setText("Hash algorithm");
        setMnemonic('H');
        addActionListener(new UserActionListener(() -> HashFunctionDialog.show(window)));
    }
}
