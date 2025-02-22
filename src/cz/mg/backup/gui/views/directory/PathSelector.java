package cz.mg.backup.gui.views.directory;

import cz.mg.annotations.classes.Component;
import cz.mg.annotations.classes.Service;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.annotations.requirement.Optional;
import cz.mg.backup.gui.MainWindow;
import cz.mg.backup.gui.event.*;
import cz.mg.backup.gui.services.ButtonFactory;
import cz.mg.collections.list.List;
import cz.mg.panel.Panel;

import javax.swing.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

public @Component class PathSelector extends Panel {
    private static final int MARGIN = 0;
    private static final int PADDING = 4;

    private final @Service ButtonFactory buttonFactory = ButtonFactory.getInstance();

    private final @Mandatory List<UserPathChangeListener> listeners = new List<>();
    private final @Mandatory MainWindow window;
    private final @Mandatory JTextField pathField;
    private final @Mandatory JFileChooser directoryChooser;
    private @Optional Path path;
    private @Optional Action action;
    private @Optional String oldText;

    public PathSelector(@Mandatory MainWindow window) {
        super(MARGIN, PADDING);
        this.window = window;

        pathField = new JTextField();
        pathField.setEditable(false);
        pathField.addMouseListener(new UserMouseDoubleClickListener(this::onMouseDoubleClicked));
        pathField.addFocusListener(new UserFocusLostListener(this::onFocusLost));
        pathField.addKeyListener(new UserKeyPressListener(this::onKeyPressed));

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
        refresh();
        triggerListeners();
    }

    private void select() {
        int result = directoryChooser.showOpenDialog(this);
        File file = directoryChooser.getSelectedFile();
        if (file != null && result == JFileChooser.APPROVE_OPTION) {
            setPath(file.toPath());
        }
    }

    private void refresh() {
        pathField.setText(path == null ? "" : path.toString());
    }

    public void addPathSelectionListener(@Mandatory UserPathChangeListener listener) {
        listeners.addLast(listener);
    }

    private void triggerListeners() {
        for (var listener : listeners) {
            listener.pathChanged(path);
        }
    }

    private void onMouseDoubleClicked(@Mandatory MouseEvent event) {
        enableChanges();
    }

    private void onFocusLost() {
        processPathChanges();
    }

    private void onKeyPressed(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.VK_ENTER) {
            action = Action.APPLY;
            processPathChanges();
        } else if (event.getKeyCode() == KeyEvent.VK_ESCAPE) {
            action = Action.CANCEL;
            processPathChanges();
        }
    }

    private void enableChanges() {
        pathField.setEditable(true);
        pathField.requestFocus();
        action = Action.CANCEL;
        oldText = pathField.getText().trim();
    }

    private void processPathChanges() {
        String newText = pathField.getText().trim();
        if (action == Action.APPLY && !Objects.equals(oldText, newText)) {
            applyChanges();
        } else {
            cancelChanges();
        }
    }

    private void applyChanges() {
        action = null;
        pathField.setEditable(false);
        setPathFromField();
        oldText = null;
    }

    private void cancelChanges() {
        action = null;
        pathField.setEditable(false);
        refresh();
        oldText = null;
    }

    private void setPathFromField() {
        String text = pathField.getText().trim();
        Path newPath = text.isEmpty() ? null : Path.of(pathField.getText());
        if (newPath != null) {
            if (!Files.exists(newPath)) {
                JOptionPane.showMessageDialog(
                    window,
                    "Selected directory does not exist.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE
                );
            } else if (!Files.isDirectory(newPath)) {
                JOptionPane.showMessageDialog(
                    window,
                    "Selected file is not a directory.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE
                );
            }
        }
        setPath(newPath);
    }

    private enum Action {
        APPLY,
        CANCEL
    }
}
