package cz.mg.backup.event;

import cz.mg.annotations.classes.Component;
import cz.mg.annotations.requirement.Mandatory;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public @Component class UserActionListener implements UserListener, ActionListener {
    private final @Mandatory Handler handler;

    public UserActionListener(@Mandatory Handler handler) {
        this.handler = handler;
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        handleExceptions(handler::run);
    }

    public interface Handler {
        void run();
    }
}
