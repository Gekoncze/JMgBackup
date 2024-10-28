package cz.mg.backup.gui.dialogs;

import cz.mg.annotations.classes.Component;
import cz.mg.annotations.classes.Service;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.backup.entities.Algorithm;
import cz.mg.backup.gui.event.UserEscapeKeyPressListener;
import cz.mg.backup.gui.MainWindow;
import cz.mg.backup.gui.services.ButtonFactory;
import cz.mg.panel.Panel;
import cz.mg.panel.settings.Alignment;

import javax.swing.*;

public @Component class HashFunctionDialog extends Dialog {
    private static final int MARGIN = 8;
    private static final int PADDING = 8;
    private static final int MIN_WIDTH = 256;

    private final @Service ButtonFactory buttonFactory = ButtonFactory.getInstance();

    private final @Mandatory MainWindow window;
    private final @Mandatory JComboBox<Algorithm> algorithmComboBox;

    private HashFunctionDialog(@Mandatory MainWindow window) {
        super(window);
        this.window = window;
        setTitle("Hash algorithm");

        algorithmComboBox = new JComboBox<>(Algorithm.values());
        algorithmComboBox.setSelectedItem(window.getSettings().getAlgorithm());

        Panel buttonsPanel = new Panel(0, PADDING, Alignment.RIGHT);
        buttonsPanel.addHorizontal(buttonFactory.create("Cancel", this::cancel));
        buttonsPanel.addHorizontal(buttonFactory.create("Ok", this::ok));

        Panel panel = new Panel(MARGIN, PADDING);
        panel.addVertical(new JLabel("Hash algorithm"));
        panel.addVertical(algorithmComboBox, 1, 1);
        panel.addVertical(buttonsPanel, 1, 0);

        getContentPane().add(panel);
        addKeyListenerRecursive(this, new UserEscapeKeyPressListener(this::cancel));
        pack();
        setSize(Math.max(getWidth(), MIN_WIDTH), getHeight());
        setLocationRelativeTo(null);
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
        new HashFunctionDialog(window).setVisible(true);
    }
}
