package cz.mg.backup.event;

import cz.mg.annotations.classes.Component;
import cz.mg.annotations.requirement.Mandatory;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public @Component class UserMouseClickListener implements UserListener, MouseListener {
    private final @Mandatory Handler handler;

    public UserMouseClickListener(@Mandatory Handler handler) {
        this.handler = handler;
    }

    @Override
    public void mouseClicked(@Mandatory MouseEvent event) {
        handleExceptions(() -> handler.run(event));
    }

    @Override
    public void mousePressed(@Mandatory MouseEvent event) {
    }

    @Override
    public void mouseReleased(@Mandatory MouseEvent event) {
    }

    @Override
    public void mouseEntered(@Mandatory MouseEvent event) {
    }

    @Override
    public void mouseExited(@Mandatory MouseEvent event) {
    }

    public interface Handler {
        void run(@Mandatory MouseEvent event);
    }
}
