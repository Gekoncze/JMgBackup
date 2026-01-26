package cz.mg.backup.gui.event;

import cz.mg.annotations.classes.Component;
import cz.mg.annotations.requirement.Mandatory;

public @Component class UserButtonListener implements UserListener {
    private final @Mandatory Handler handler;

    public UserButtonListener(@Mandatory Handler handler) {
        this.handler = handler;
    }

    public void buttonClicked() {
        handleExceptions(() -> handler.run());
    }

    public interface Handler {
        void run();
    }
}
