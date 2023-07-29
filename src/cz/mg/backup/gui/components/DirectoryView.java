package cz.mg.backup.gui.components;

import cz.mg.annotations.classes.Component;
import cz.mg.annotations.classes.Service;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.annotations.requirement.Optional;
import cz.mg.backup.entities.Directory;
import cz.mg.backup.event.UserActionListener;
import cz.mg.backup.gui.MainWindow;
import cz.mg.backup.gui.components.model.NodeCellRenderer;
import cz.mg.backup.gui.components.model.ObjectTreeModel;
import cz.mg.backup.gui.dialog.TaskDialog;
import cz.mg.backup.gui.services.DirectoryTreeFactory;
import cz.mg.backup.services.DirectoryReader;
import cz.mg.panel.Panel;

import javax.swing.*;
import java.io.File;
import java.nio.file.Path;

public @Component class DirectoryView extends Panel {
    private static final int MARGIN = 4;
    private static final int PADDING = 4;

    private final @Service DirectoryReader directoryReader = DirectoryReader.getInstance();
    private final @Service DirectoryTreeFactory directoryTreeFactory = DirectoryTreeFactory.getInstance();

    private final @Mandatory MainWindow window;
    private final @Mandatory JTextField pathField = new JTextField();
    private final @Mandatory JTree treeView = new JTree();
    private final @Mandatory JFileChooser directoryChooser = new JFileChooser();

    private @Optional Path path;
    private @Optional Directory directory;

    public DirectoryView(@Mandatory MainWindow window) {
        this.window = window;
        setMargin(MARGIN);
        setPadding(PADDING);
        Panel pathPanel = new Panel(0, PADDING);
        pathField.setEditable(false);
        pathPanel.addHorizontal(pathField, 1, 0);
        JButton selectButton = new JButton("...");
        selectButton.addActionListener(new UserActionListener(this::select));
        pathPanel.addHorizontal(selectButton);
        addVertical(pathPanel, 1, 0);
        treeView.setBorder(BorderFactory.createEtchedBorder());
        addVertical(treeView, 1, 1);
        directoryChooser.setDialogType(JFileChooser.OPEN_DIALOG);
        directoryChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        refresh();
    }

    public @Optional Path getPath() {
        return path;
    }

    public void setPath(@Optional Path path) {
        this.path = path;
        pathField.setText(path == null ? "" : path.toString());
    }

    private void select() {
        directoryChooser.showOpenDialog(this);
        File file = directoryChooser.getSelectedFile();
        if (file != null) {
            setPath(file.toPath());
        }
        reload();
    }

    public void reload() {
        if (path != null) {
            TaskDialog.show(
                window,
                "Load Directory",
                () -> directory = directoryReader.read(path, window.getSettings())
            );
        } else {
            directory = null;
        }
        refresh();
    }

    private void refresh() {
        if (directory != null) {
            treeView.setModel(new ObjectTreeModel(directoryTreeFactory.create(directory)));
        } else {
            treeView.setModel(new ObjectTreeModel(null));
        }
        treeView.setCellRenderer(new NodeCellRenderer());
    }
}
