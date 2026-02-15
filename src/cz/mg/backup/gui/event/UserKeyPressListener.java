package cz.mg.backup.gui.event;

import cz.mg.annotations.classes.Component;
import cz.mg.annotations.requirement.Mandatory;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public @Component class UserKeyPressListener implements UserListener, KeyListener {
    private final int key;
    private final @Mandatory Handler handler;

    public UserKeyPressListener(int key, @Mandatory Handler handler) {
        this.key = key;
        this.handler = handler;
    }

    @Override
    public void keyTyped(@Mandatory KeyEvent event) {
    }

    @Override
    public void keyPressed(@Mandatory KeyEvent event) {
        if (event.getKeyCode() == key) {
            handleExceptions(() -> handler.run());
        }
    }

    @Override
    public void keyReleased(@Mandatory KeyEvent event) {
    }

    public interface Handler {
        void run();
    }
}
