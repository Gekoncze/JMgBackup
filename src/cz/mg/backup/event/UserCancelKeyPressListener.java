package cz.mg.backup.event;

import cz.mg.annotations.classes.Component;
import cz.mg.annotations.requirement.Mandatory;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public @Component class UserCancelKeyPressListener implements UserListener, KeyListener {
    private final @Mandatory Handler handler;

    public UserCancelKeyPressListener(@Mandatory Handler handler) {
        this.handler = handler;
    }

    @Override
    public void keyTyped(@Mandatory KeyEvent event) {
    }

    @Override
    public void keyPressed(@Mandatory KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.VK_ESCAPE) {
            handleExceptions(handler::run);
        }
    }

    @Override
    public void keyReleased(@Mandatory KeyEvent event) {
    }

    public interface Handler {
        void run();
    }
}
