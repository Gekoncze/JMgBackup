package cz.mg.backup.exceptions;

import cz.mg.annotations.classes.Error;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.backup.entities.File;

public @Error class MoveException extends RuntimeException {
    private final @Mandatory File suspect;

    public MoveException(@Mandatory File suspect) {
        super("Possible moved file found!");
        this.suspect = suspect;
    }

    public @Mandatory File getSuspect() {
        return suspect;
    }
}
