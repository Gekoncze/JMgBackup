package cz.mg.backup.gui.dialogs;

import cz.mg.annotations.classes.Component;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.backup.Info;
import cz.mg.backup.gui.event.UserEscapeKeyPressListener;
import cz.mg.backup.gui.MainWindow;
import cz.mg.backup.gui.components.UrlLabel;
import cz.mg.panel.Panel;
import cz.mg.panel.settings.Alignment;
import cz.mg.panel.settings.Fill;

import javax.swing.*;

public @Component class AboutDialog extends Dialog {
    private static final int MARGIN = 8;
    private static final int PADDING = 8;

    private AboutDialog(@Mandatory MainWindow window) {
        super(window);
        setTitle("About");

        Panel panel = new Panel(MARGIN, PADDING);
        panel.addVertical(new JLabel(Info.NAME), 0, 0, Alignment.MIDDLE, Fill.NONE);
        panel.addVertical(new JLabel("Version " + Info.VERSION), 0, 0, Alignment.MIDDLE, Fill.NONE);
        panel.addVertical(new UrlLabel(Info.URL), 0, 0, Alignment.MIDDLE, Fill.NONE);

        getContentPane().add(panel);
        addKeyListenerRecursive(this, new UserEscapeKeyPressListener(this::dispose));
        pack();
        setLocationRelativeTo(null);
    }

    public static void show(@Mandatory MainWindow window) {
        new AboutDialog(window).setVisible(true);
    }
}
