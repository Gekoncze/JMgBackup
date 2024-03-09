package cz.mg.backup.gui;

import cz.mg.annotations.classes.Component;
import cz.mg.annotations.classes.Service;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.backup.Info;
import cz.mg.backup.entities.Settings;
import cz.mg.backup.gui.components.details.DetailsView;
import cz.mg.backup.gui.components.DirectoryView;
import cz.mg.backup.gui.components.dialog.TaskDialog;
import cz.mg.backup.gui.components.menu.MainMenuBar;
import cz.mg.backup.services.DirectoryCompareService;
import cz.mg.backup.services.DirectoryErrorService;
import cz.mg.panel.Panel;

import javax.swing.*;
import java.awt.*;

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
    private final @Mandatory DetailsView detailsView;

    public MainWindow() {
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setTitle(TITLE);
        setSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);
        setLocationRelativeTo(null);
        setJMenuBar(new MainMenuBar(this));

        Panel panel = new Panel(MARGIN, PADDING);

        JSplitPane compareSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        compareSplitPane.setLeftComponent(leftView = new DirectoryView(this));
        compareSplitPane.setRightComponent(rightView = new DirectoryView(this));
        compareSplitPane.setResizeWeight(0.5);
        compareSplitPane.setMinimumSize(new Dimension(0, 0));

        JSplitPane detailsSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        detailsSplitPane.setTopComponent(compareSplitPane);
        detailsSplitPane.setBottomComponent(detailsView = new DetailsView());
        detailsSplitPane.setResizeWeight(1);
        detailsSplitPane.setDividerLocation(DEFAULT_HEIGHT - 192);
        panel.addVertical(detailsSplitPane, 1, 1);

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

    public @Mandatory DetailsView getDetailsView() {
        return detailsView;
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

    public void reload() {
        leftView.reload();
        rightView.reload();
    }

    public static void main(String[] args) {
        new MainWindow().setVisible(true);
    }
}
