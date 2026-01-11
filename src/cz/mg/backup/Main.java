package cz.mg.backup;

import cz.mg.annotations.classes.Static;
import cz.mg.backup.gui.MainWindow;

public @Static class Main {
    public static void main(String[] args) {
        new MainWindow().setVisible(true);
    }
}
