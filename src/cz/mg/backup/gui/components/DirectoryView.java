package cz.mg.backup.gui.components;

import cz.mg.annotations.classes.Component;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.annotations.requirement.Optional;
import cz.mg.backup.event.UserActionListener;
import cz.mg.panel.Panel;

import javax.swing.*;
import java.io.File;
import java.nio.file.Path;

public @Component class DirectoryView extends Panel {
    private static final int MARGIN = 4;
    private static final int PADDING = 4;

    private @Optional Path path;
    private final @Mandatory JTextField pathField = new JTextField();
    private final @Mandatory JTree treeView = new JTree();
    private final @Mandatory JFileChooser directoryChooser = new JFileChooser();

    public DirectoryView() {
        setMargin(MARGIN);
        setPadding(PADDING);
        Panel pathPanel = new Panel(0, PADDING);
        pathField.setEditable(false);
        pathPanel.addHorizontal(pathField, 1, 0);
        JButton selectButton = new JButton("...");
        selectButton.addActionListener(new UserActionListener(this::selectPath));
        pathPanel.addHorizontal(selectButton);
        addVertical(pathPanel, 1, 0);
        treeView.setBorder(BorderFactory.createEtchedBorder());
        addVertical(treeView, 1, 1);
        directoryChooser.setDialogType(JFileChooser.OPEN_DIALOG);
        directoryChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
    }

    private void selectPath() {
        directoryChooser.showOpenDialog(this);
        File file = directoryChooser.getSelectedFile();
        if (file != null) {
            setPath(file.toPath());
        }
    }

    public @Optional Path getPath() {
        return path;
    }

    public void setPath(@Optional Path path) {
        this.path = path;
        pathField.setText(path == null ? "" : path.toString());
    }
}
