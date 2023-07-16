package cz.mg.backup.event;

import cz.mg.annotations.classes.Component;
import cz.mg.annotations.requirement.Mandatory;

import javax.swing.*;

public @Component interface UserListener {
    default void handleExceptions(@Mandatory Runnable runnable) {
        try {
            runnable.run();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(
                null,
                e.getMessage(),
                e.getClass().getSimpleName(),
                JOptionPane.ERROR_MESSAGE
            );

            e.printStackTrace();
        }
    }
}
