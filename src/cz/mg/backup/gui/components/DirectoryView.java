package cz.mg.backup.gui.components;

import cz.mg.annotations.classes.Component;
import cz.mg.annotations.classes.Service;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.annotations.requirement.Optional;
import cz.mg.backup.entities.Directory;
import cz.mg.backup.gui.MainWindow;
import cz.mg.backup.gui.components.dialog.TaskDialog;
import cz.mg.backup.gui.components.model.ObjectTreeModel;
import cz.mg.backup.gui.services.ButtonFactory;
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
    private final @Service ButtonFactory buttonFactory = ButtonFactory.getInstance();

    private final @Mandatory MainWindow window;
    private final @Mandatory JTextField pathField;
    private final @Mandatory JTree treeView;
    private final @Mandatory JFileChooser directoryChooser;

    private @Optional Path path;
    private @Optional Directory directory;

    public DirectoryView(@Mandatory MainWindow window) {
        this.window = window;
        setMargin(MARGIN);
        setPadding(PADDING);

        pathField = new JTextField();
        pathField.setEditable(false);

        Panel pathPanel = new Panel(0, PADDING);
        pathPanel.addHorizontal(pathField, 1, 0);
        pathPanel.addHorizontal(buttonFactory.create("...", this::select));

        treeView = new JTree();
        treeView.setBorder(BorderFactory.createEtchedBorder());

        addVertical(pathPanel, 1, 0);
        addVertical(new JScrollPane(treeView), 1, 1);

        directoryChooser = new JFileChooser();
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

    public @Optional Directory getDirectory() {
        return directory;
    }

    public void setDirectory(@Optional Directory directory) {
        this.directory = directory;
        refresh();
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
                () -> setDirectory(directoryReader.read(path, window.getSettings()))
            );
        } else {
            setDirectory(null);
        }

        window.compare();
    }

    public void refresh() {
        if (directory != null) {
            treeView.setModel(new ObjectTreeModel(directoryTreeFactory.create(directory)));
        } else {
            treeView.setModel(new ObjectTreeModel(null));
        }
        treeView.setCellRenderer(new NodeCellRenderer());
    }
}
