package cz.mg.backup.gui.components;

import cz.mg.annotations.classes.Component;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.backup.gui.actions.Action;
import cz.mg.backup.gui.event.UserActionListener;

import javax.swing.*;

public @Component class ActionButton extends JButton {
    public ActionButton(@Mandatory Action action) {
        if (action.getLargeIcon() != null) setIcon(action.getLargeIcon());
        setToolTipText(action.getName());
        addActionListener(new UserActionListener(action::run));
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
