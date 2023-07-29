package cz.mg.backup.gui.components.dialog;

import cz.mg.annotations.classes.Component;
import cz.mg.annotations.classes.Service;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.annotations.requirement.Optional;
import cz.mg.backup.event.UserActionListener;
import cz.mg.backup.event.UserEscapeKeyPressListener;
import cz.mg.backup.gui.MainWindow;
import cz.mg.backup.gui.services.ButtonFactory;
import cz.mg.panel.Panel;
import cz.mg.panel.settings.Alignment;

import javax.swing.*;

public @Component class HashFunctionDialog extends Dialog {
    private static final int MARGIN = 8;
    private static final int PADDING = 8;
    private static final int NAME_FIELD_SIZE = 24;

    private final @Service ButtonFactory buttonFactory = ButtonFactory.getInstance();

    private final @Mandatory MainWindow window;
    private final @Mandatory JTextField nameField;
    private final @Mandatory JButton chooserButton;

    private HashFunctionDialog(@Mandatory MainWindow window) {
        super(window);
        this.window = window;
        setTitle("Hash algorithm");

        nameField = new JTextField();
        nameField.setColumns(NAME_FIELD_SIZE);
        nameField.setText(nameToRaw(window.getSettings().getHashAlgorithm()));

        Panel namePanel = new Panel(0, PADDING);
        namePanel.addHorizontal(new JLabel("Name"));
        namePanel.addHorizontal(nameField, 1, 0);
        namePanel.addHorizontal(chooserButton = buttonFactory.create("...", this::choose));

        Panel buttonsPanel = new Panel(0, PADDING, Alignment.RIGHT);
        buttonsPanel.addHorizontal(buttonFactory.create("Cancel", this::cancel));
        buttonsPanel.addHorizontal(buttonFactory.create("Ok", this::ok));

        Panel panel = new Panel(MARGIN, PADDING);
        panel.addVertical(namePanel, 1, 1);
        panel.addVertical(buttonsPanel, 1, 0);

        getContentPane().add(panel);
        addKeyListenerRecursive(this, new UserEscapeKeyPressListener(this::cancel));
        pack();
        setLocationRelativeTo(null);
    }

    private void choose() {
        JPopupMenu menu = new JPopupMenu();
        menu.add(createOptionItem("MD5"));
        menu.add(createOptionItem("SHA-1"));
        menu.add(createOptionItem("SHA-256"));
        menu.show(chooserButton, 0, 0);
    }

    private void cancel() {
        dispose();
    }

    private void ok() {
        window.getSettings().setHashAlgorithm(rawToName(nameField.getText()));
        dispose();
    }

    private @Optional String rawToName(@Mandatory String text) {
        String raw = text.trim();
        return raw.isEmpty() ? null : raw;
    }

    private @Mandatory String nameToRaw(@Optional String name) {
        return name == null ? "" : name;
    }

    private @Mandatory JMenuItem createOptionItem(@Mandatory String value) {
        JMenuItem item = new JMenuItem();
        item.setText(value);
        item.addActionListener(new UserActionListener(() -> nameField.setText(value)));
        return item;
    }

    public static void show(@Mandatory MainWindow window) {
        new HashFunctionDialog(window).setVisible(true);
    }
}
