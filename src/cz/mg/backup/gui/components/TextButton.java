package cz.mg.backup.gui.components;

import cz.mg.annotations.requirement.Mandatory;
import cz.mg.backup.gui.event.UserActionListener;

import javax.swing.*;

public class TextButton extends JButton {
    public TextButton(@Mandatory String text) {
        setText(text);
    }

    public TextButton(@Mandatory String text, @Mandatory UserActionListener.Handler action) {
        setText(text);
        addActionListener(new UserActionListener(action));
    }
}
