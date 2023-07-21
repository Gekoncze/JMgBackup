package cz.mg.backup.gui;

import cz.mg.annotations.classes.Component;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.backup.Info;
import cz.mg.backup.entities.Settings;
import cz.mg.backup.gui.components.DirectoryView;
import cz.mg.backup.gui.menu.MainMenuBar;
import cz.mg.panel.Panel;

import javax.swing.*;

public @Component class MainWindow extends JFrame {
    private static final @Mandatory String TITLE = Info.NAME + " " + Info.VERSION;
    private static final int DEFAULT_WIDTH = 800;
    private static final int DEFAULT_HEIGHT = 600;
    private static final int MARGIN = 0;
    private static final int PADDING = 8;

    private final @Mandatory DirectoryView leftView = new DirectoryView();
    private final @Mandatory DirectoryView rightView = new DirectoryView();

    private final @Mandatory Settings settings = new Settings();

    public MainWindow() {
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setTitle(TITLE);
        setSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);
        setLocationRelativeTo(null);
        setJMenuBar(new MainMenuBar(this));
        Panel panel = new Panel(MARGIN, PADDING);
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setLeftComponent(leftView);
        splitPane.setRightComponent(rightView);
        splitPane.setResizeWeight(0.5);
        panel.addVertical(splitPane, 1, 1);
        getContentPane().add(panel);
    }

    public @Mandatory Settings getSettings() {
        return settings;
    }

    public static void main(String[] args) {
        new MainWindow().setVisible(true);
    }
}
