package cz.mg.backup.gui.event;

import cz.mg.annotations.classes.Component;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.annotations.requirement.Optional;

import java.nio.file.Path;

public @Component class UserPathChangeListener implements UserListener {
    private final @Mandatory Handler handler;

    public UserPathChangeListener(@Mandatory Handler handler) {
        this.handler = handler;
    }

    public void pathChanged(@Optional Path path) {
        handleExceptions(() -> handler.run(path));
    }

    public interface Handler {
        void run(@Optional Path path);
    }
}
