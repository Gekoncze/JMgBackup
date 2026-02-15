package cz.mg.backup.gui.components;

import cz.mg.annotations.classes.Component;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.backup.gui.actions.Action;
import cz.mg.backup.gui.event.UserActionListener;

import javax.swing.*;

public @Component class ActionItem extends JMenuItem {
    public ActionItem(@Mandatory Action action) {
        if (action.getSmallIcon() != null) setIcon(action.getSmallIcon());
        setText(action.getName());
        if (action.getMnemonic() != null) setMnemonic(action.getMnemonic());
        if (action.getShortcut() != null) setAccelerator(action.getShortcut());
        addActionListener(new UserActionListener(action::run));
    }
}
