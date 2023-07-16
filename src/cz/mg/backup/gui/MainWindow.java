package cz.mg.backup.gui;

import cz.mg.annotations.classes.Component;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.backup.Info;
import cz.mg.backup.gui.menu.MainMenuBar;

import javax.swing.*;

public @Component class MainWindow extends JFrame {
    private static final @Mandatory String TITLE = Info.NAME + " " + Info.VERSION;
    private static final int DEFAULT_WIDTH = 800;
    private static final int DEFAULT_HEIGHT = 600;

    public MainWindow() {
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setTitle(TITLE);
        setSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);
        setLocationRelativeTo(null);
        setJMenuBar(new MainMenuBar(this));
    }

    public static void main(String[] args) {
        new MainWindow().setVisible(true);
    }
}
