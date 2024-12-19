package cz.mg.backup.gui;

import cz.mg.annotations.classes.Component;
import cz.mg.annotations.classes.Service;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.backup.Info;
import cz.mg.backup.entities.Algorithm;
import cz.mg.backup.entities.Directory;
import cz.mg.backup.entities.Settings;
import cz.mg.backup.gui.views.details.DetailsView;
import cz.mg.backup.gui.views.directory.DirectoryView;
import cz.mg.backup.gui.dialogs.ProgressDialog;
import cz.mg.backup.gui.menu.MainMenuBar;
import cz.mg.backup.services.DirectoryComparator;
import cz.mg.backup.services.StatisticsCounter;
import cz.mg.panel.Panel;

import javax.swing.*;
import java.awt.*;

public @Component class MainWindow extends JFrame {
    private static final @Mandatory String TITLE = Info.NAME + " " + Info.VERSION;
    private static final int DEFAULT_WIDTH = 1024;
    private static final int DEFAULT_HEIGHT = 768;
    private static final int DEFAULT_DETAILS_HEIGHT = 220;
    private static final int MARGIN = 0;
    private static final int PADDING = 8;

    private final @Service DirectoryComparator directoryComparator = DirectoryComparator.getInstance();
    private final @Service StatisticsCounter statisticsCounter = StatisticsCounter.getInstance();

    private final @Mandatory Settings settings = new Settings(Algorithm.SHA256);
    private final @Mandatory DirectoryView leftView;
    private final @Mandatory DirectoryView rightView;
    private final @Mandatory DetailsView detailsView;

    private boolean compareLock = false;

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
        detailsSplitPane.setDividerLocation(DEFAULT_HEIGHT - DEFAULT_DETAILS_HEIGHT);
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
        if (!compareLock) {
            Directory a = leftView.getDirectory();
            Directory b = rightView.getDirectory();

            ProgressDialog.run(
                this,
                "Compare",
                null,
                progress -> directoryComparator.compare(a, b, progress)
            );

            ProgressDialog.run(
                this,
                "Gather statistics",
                null,
                progress -> statisticsCounter.count(a)
            );

            ProgressDialog.run(
                this,
                "Gather statistics",
                null,
                progress -> statisticsCounter.count(b)
            );

            leftView.refresh();
            rightView.refresh();
            detailsView.refresh();
        }
    }

    public void reload() {
        try {
            compareLock = true;
            leftView.reload();
            rightView.reload();
        } finally {
            compareLock = false;
        }

        compare();
    }

    public static void main(String[] args) {
        new MainWindow().setVisible(true);
    }
}
