package cz.mg.backup.event;

import cz.mg.annotations.classes.Component;
import cz.mg.annotations.requirement.Mandatory;

import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

public @Component class UserWindowClosedListener implements UserListener, WindowListener {
    private final @Mandatory Handler handler;

    public UserWindowClosedListener(@Mandatory Handler handler) {
        this.handler = handler;
    }

    @Override
    public void windowOpened(@Mandatory WindowEvent event) {
    }

    @Override
    public void windowClosing(@Mandatory WindowEvent event) {
    }

    @Override
    public void windowClosed(@Mandatory WindowEvent event) {
        handleExceptions(handler::run);
    }

    @Override
    public void windowIconified(@Mandatory WindowEvent event) {
    }

    @Override
    public void windowDeiconified(@Mandatory WindowEvent event) {
    }

    @Override
    public void windowActivated(@Mandatory WindowEvent event) {
    }

    @Override
    public void windowDeactivated(@Mandatory WindowEvent event) {
    }

    public interface Handler {
        void run();
    }
}
