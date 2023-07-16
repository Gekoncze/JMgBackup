package cz.mg.backup.gui.dialog;

import cz.mg.annotations.classes.Component;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.backup.Info;
import cz.mg.backup.gui.MainWindow;
import cz.mg.backup.gui.components.UrlLabel;
import cz.mg.panel.Panel;
import cz.mg.panel.settings.Alignment;
import cz.mg.panel.settings.Fill;

import javax.swing.*;

public @Component class AboutDialog extends JDialog {
    private static final int BORDER = 8;
    private static final int PADDING = 8;

    public AboutDialog(@Mandatory MainWindow window) {
        super(window, false);
        setTitle("About");
        Panel panel = new Panel(BORDER, PADDING);
        panel.addVertical(new JLabel(Info.NAME), 0, 0, Alignment.MIDDLE, Fill.NONE);
        panel.addVertical(new JLabel("Version " + Info.VERSION), 0, 0, Alignment.MIDDLE, Fill.NONE);
        panel.addVertical(new UrlLabel(Info.URL), 0, 0, Alignment.MIDDLE, Fill.NONE);
        getContentPane().add(panel);
        pack();
        setLocationRelativeTo(null);
    }
}
