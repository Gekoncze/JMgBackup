package cz.mg.backup.gui;

import cz.mg.annotations.classes.Component;
import cz.mg.annotations.classes.Service;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.backup.Info;
import cz.mg.backup.entities.Settings;
import cz.mg.backup.gui.components.DirectoryView;
import cz.mg.backup.gui.components.dialog.TaskDialog;
import cz.mg.backup.gui.components.menu.MainMenuBar;
import cz.mg.backup.services.DirectoryCompareService;
import cz.mg.backup.services.DirectoryErrorService;
import cz.mg.panel.Panel;

import javax.swing.*;

public @Component class MainWindow extends JFrame {
    private static final @Mandatory String TITLE = Info.NAME + " " + Info.VERSION;
    private static final int DEFAULT_WIDTH = 800;
    private static final int DEFAULT_HEIGHT = 600;
    private static final int MARGIN = 0;
    private static final int PADDING = 8;

    private final @Service DirectoryCompareService compareService = DirectoryCompareService.getInstance();
    private final @Service DirectoryErrorService errorService = DirectoryErrorService.getInstance();

    private final @Mandatory Settings settings = new Settings();
    private final @Mandatory DirectoryView leftView;
    private final @Mandatory DirectoryView rightView;

    public MainWindow() {
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setTitle(TITLE);
        setSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);
        setLocationRelativeTo(null);
        setJMenuBar(new MainMenuBar(this));
        Panel panel = new Panel(MARGIN, PADDING);
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setLeftComponent(leftView = new DirectoryView(this));
        splitPane.setRightComponent(rightView = new DirectoryView(this));
        splitPane.setResizeWeight(0.5);
        panel.addVertical(splitPane, 1, 1);
        getContentPane().add(panel);
    }

    public @Mandatory Settings getSettings() {
        return settings;
    }

    public @Mandatory DirectoryView getLeftView() {
        return leftView;
    }

    public @Mandatory DirectoryView getRightView() {
        return rightView;
    }

    public void compare() {
        TaskDialog.show(this, "Compare", () -> compareService.compare(
            leftView.getDirectory(),
            rightView.getDirectory()
        ));

        TaskDialog.show(this, "Propagate", () -> {
            if (leftView.getDirectory() != null) {
                errorService.propagate(leftView.getDirectory());
            }

            if (rightView.getDirectory() != null) {
                errorService.propagate(rightView.getDirectory());
            }
        });

        leftView.refresh();
        rightView.refresh();
    }

    public static void main(String[] args) {
        new MainWindow().setVisible(true);
    }
}
