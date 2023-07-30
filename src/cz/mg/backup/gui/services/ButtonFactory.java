package cz.mg.backup.gui.services;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.backup.gui.event.UserActionListener;

import javax.swing.*;

public @Service class ButtonFactory {
    private static volatile @Service ButtonFactory instance;

    public static @Service ButtonFactory getInstance() {
        if (instance == null) {
            synchronized (Service.class) {
                if (instance == null) {
                    instance = new ButtonFactory();
                }
            }
        }
        return instance;
    }

    private ButtonFactory() {
    }

    public @Mandatory JButton create(@Mandatory String label, @Mandatory UserActionListener.Handler action) {
        JButton button = new JButton();
        button.setText(label);
        button.addActionListener(new UserActionListener(action));
        return button;
    }
}
