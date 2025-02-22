package cz.mg.backup.gui.event;

import cz.mg.annotations.classes.Component;
import cz.mg.annotations.requirement.Mandatory;

import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

public @Component class UserFocusLostListener implements UserListener, FocusListener {
    private final @Mandatory Handler handler;

    public UserFocusLostListener(@Mandatory Handler handler) {
        this.handler = handler;
    }

    @Override
    public void focusGained(@Mandatory FocusEvent focusEvent) {

    }

    @Override
    public void focusLost(@Mandatory FocusEvent focusEvent) {
        handleExceptions(handler::run);
    }

    public interface Handler {
        void run();
    }
}
