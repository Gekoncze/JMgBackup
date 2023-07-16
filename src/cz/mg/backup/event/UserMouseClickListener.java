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
    public void mouseClicked(MouseEvent event) {
        handleExceptions(() -> handler.run(event));
    }

    @Override
    public void mousePressed(MouseEvent event) {
    }

    @Override
    public void mouseReleased(MouseEvent event) {
    }

    @Override
    public void mouseEntered(MouseEvent event) {
    }

    @Override
    public void mouseExited(MouseEvent event) {
    }

    public interface Handler {
        void run(@Mandatory MouseEvent event);
    }
}
