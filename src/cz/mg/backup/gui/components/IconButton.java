package cz.mg.backup.gui.components;

import cz.mg.annotations.requirement.Mandatory;
import cz.mg.backup.gui.event.UserActionListener;

import javax.swing.*;

public class IconButton extends JButton {
    public IconButton(@Mandatory Icon icon) {
        setIcon(icon);
        removeBackground();
    }

    public IconButton(@Mandatory Icon icon, @Mandatory UserActionListener.Handler action) {
        setIcon(icon);
        addActionListener(new UserActionListener(action));
        removeBackground();
    }

    private void removeBackground() {
        setBorder(null);
        setBackground(null);
        setOpaque(false);
        setContentAreaFilled(false);
        setBorderPainted(false);
    }
}
