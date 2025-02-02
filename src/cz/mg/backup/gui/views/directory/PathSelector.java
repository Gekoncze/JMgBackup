package cz.mg.backup.gui.views.directory;

import cz.mg.annotations.classes.Component;
import cz.mg.annotations.classes.Service;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.annotations.requirement.Optional;
import cz.mg.backup.gui.event.UserPathChangeListener;
import cz.mg.backup.gui.services.ButtonFactory;
import cz.mg.collections.list.List;
import cz.mg.panel.Panel;

import javax.swing.*;
import java.io.File;
import java.nio.file.Path;

public @Component class PathSelector extends Panel {
    private static final int MARGIN = 0;
    private static final int PADDING = 4;

    private final @Service ButtonFactory buttonFactory = ButtonFactory.getInstance();

    private final @Mandatory List<UserPathChangeListener> listeners = new List<>();
    private final @Mandatory JTextField pathField;
    private final @Mandatory JFileChooser directoryChooser;
    private @Optional Path path;

    public PathSelector() {
        super(MARGIN, PADDING);

        pathField = new JTextField();
        pathField.setEditable(false);

        addHorizontal(pathField, 1, 0);
        addHorizontal(buttonFactory.create("...", this::select));

        directoryChooser = new JFileChooser();
        directoryChooser.setDialogType(JFileChooser.OPEN_DIALOG);
        directoryChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
    }

    public @Optional Path getPath() {
        return path;
    }

    public void setPath(@Optional Path path) {
        this.path = path;
        pathField.setText(path == null ? "" : path.toString());
        triggerListeners();
    }

    private void select() {
        int result = directoryChooser.showOpenDialog(this);
        File file = directoryChooser.getSelectedFile();
        if (file != null && result == JFileChooser.APPROVE_OPTION) {
            setPath(file.toPath());
        }
    }

    public void addPathSelectionListener(@Mandatory UserPathChangeListener listener) {
        listeners.addLast(listener);
    }

    private void triggerListeners() {
        for (var listener : listeners) {
            listener.pathChanged(path);
        }
    }
}
