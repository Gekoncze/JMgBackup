package cz.mg.backup.gui.dialogs;

import cz.mg.annotations.classes.Component;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.backup.entities.Algorithm;
import cz.mg.backup.gui.components.TextButton;
import cz.mg.backup.gui.event.UserEscapeKeyPressListener;
import cz.mg.backup.gui.MainWindow;
import cz.mg.panel.Panel;
import cz.mg.panel.settings.Alignment;

import javax.swing.*;

public @Component class SettingsDialog extends Dialog {
    private static final int MARGIN = 8;
    private static final int PADDING = 8;
    private static final int MIN_WIDTH = 256;

    private final @Mandatory MainWindow window;
    private final @Mandatory JComboBox<Algorithm> algorithmComboBox;

    private SettingsDialog(@Mandatory MainWindow window) {
        super(window);
        this.window = window;
        setTitle("Settings");

        algorithmComboBox = new JComboBox<>(Algorithm.values());
        algorithmComboBox.setSelectedItem(window.getSettings().getAlgorithm());

        Panel panel = new Panel(MARGIN, PADDING);

        Panel hashAlgorithmPanel = new Panel(0, PADDING, Alignment.LEFT);
        hashAlgorithmPanel.addHorizontal(new JLabel("Hash algorithm"));
        hashAlgorithmPanel.addHorizontal(algorithmComboBox, 1, 1);
        panel.addVertical(hashAlgorithmPanel, 1, 0);

        Panel buttonsPanel = new Panel(0, PADDING, Alignment.RIGHT);
        buttonsPanel.addHorizontal(new TextButton("Cancel", this::cancel));
        buttonsPanel.addHorizontal(new TextButton("Ok", this::ok));
        panel.addVertical(buttonsPanel, 1, 0);

        getContentPane().add(panel);
        addKeyListenerRecursive(this, new UserEscapeKeyPressListener(this::cancel));
        pack();
        setSize(Math.max(getWidth(), MIN_WIDTH), getHeight());
        center();
    }

    private Algorithm getSelectedAlgorithm() {
        return (Algorithm) algorithmComboBox.getSelectedItem();
    }

    private void cancel() {
        dispose();
    }

    private void ok() {
        window.getSettings().setAlgorithm(getSelectedAlgorithm());
        dispose();
    }

    public static void show(@Mandatory MainWindow window) {
        new SettingsDialog(window).setVisible(true);
    }
}
