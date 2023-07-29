package cz.mg.backup.gui.components.dialog;

import cz.mg.annotations.classes.Component;
import cz.mg.annotations.requirement.Mandatory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyListener;

public @Component class Dialog extends JDialog {
    public Dialog(@Mandatory JFrame window) {
        super(window, true);
    }

    protected void addKeyListenerRecursive(@Mandatory java.awt.Component component, @Mandatory KeyListener listener) {
        component.addKeyListener(listener);
        if (component instanceof Container) {
            Container container = (Container) component;
            for (java.awt.Component child : container.getComponents()) {
                addKeyListenerRecursive(child, listener);
            }
        }
    }
}
