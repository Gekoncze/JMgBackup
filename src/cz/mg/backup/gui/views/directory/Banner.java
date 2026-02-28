package cz.mg.backup.gui.views.directory;

import cz.mg.annotations.classes.Component;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.panel.Panel;
import cz.mg.panel.settings.Alignment;

import javax.swing.*;
import java.awt.*;

public @Component class Banner extends Panel {
    private static final int MARGIN = 4;
    private static final int PADDING = 4;

    public static final Color ERROR = new Color(255, 180, 180);
    public static final Color WARNING = new Color(255, 255, 180);
    public static final Color INFO = new Color(180, 180, 255);

    private final @Mandatory JLabel label = new JLabel();

    public Banner() {
        super(MARGIN, PADDING, Alignment.LEFT);
        addVertical(label, 1, 1);
        setVisible(false);
        setOpaque(true);
    }

    public void show(@Mandatory String message, @Mandatory Color color) {
        label.setText(message);
        setBackground(color);
        setVisible(true);
    }

    public void close() {
        setVisible(false);
    }
}
