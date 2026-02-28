package cz.mg.backup.gui.dialogs;

import cz.mg.annotations.classes.Component;
import cz.mg.annotations.requirement.Mandatory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyListener;

public abstract @Component class Dialog extends JDialog {
    private final @Mandatory JFrame window;

    public Dialog(@Mandatory JFrame window) {
        super(window, true);
        this.window = window;
    }

    protected void addKeyListenerRecursive(@Mandatory java.awt.Component component, @Mandatory KeyListener listener) {
        component.addKeyListener(listener);
        if (component instanceof Container container) {
            for (java.awt.Component child : container.getComponents()) {
                addKeyListenerRecursive(child, listener);
            }
        }
    }

    protected void center() {
        int wx = window.getX();
        int wy = window.getY();
        int ww = window.getWidth();
        int wh = window.getHeight();
        int w = getWidth();
        int h = getHeight();
        int x = wx + (ww - w) / 2;
        int y = wy + (wh - h) / 2;
        setLocation(x, y);
    }
}
