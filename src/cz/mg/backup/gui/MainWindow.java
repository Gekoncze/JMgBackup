package cz.mg.backup.gui;

import cz.mg.annotations.classes.Component;
import cz.mg.annotations.requirement.Mandatory;

import javax.swing.*;

public @Component class MainWindow extends JFrame {
    private static final @Mandatory String NAME = "JMgBackup";
    private static final @Mandatory String TITLE = NAME + " " + Version.getInstance();
    private static final int DEFAULT_WIDTH = 800;
    private static final int DEFAULT_HEIGHT = 600;

    public MainWindow() {
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setTitle(TITLE);
        setSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);
        setLocationRelativeTo(null);
    }

    public static void main(String[] args) {
        new MainWindow().setVisible(true);
    }
}
