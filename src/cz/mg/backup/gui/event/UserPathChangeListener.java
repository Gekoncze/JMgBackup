package cz.mg.backup.gui.event;

import cz.mg.annotations.classes.Component;
import cz.mg.annotations.requirement.Mandatory;

public @Component class UserPathChangeListener implements UserListener {
    private final @Mandatory Handler handler;

    public UserPathChangeListener(@Mandatory Handler handler) {
        this.handler = handler;
    }

    public void pathChanged() {
        handleExceptions(() -> handler.run());
    }

    public interface Handler {
        void run();
    }
}
