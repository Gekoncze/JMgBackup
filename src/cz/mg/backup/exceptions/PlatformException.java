package cz.mg.backup.exceptions;

import cz.mg.annotations.classes.Error;
import cz.mg.annotations.requirement.Mandatory;

public @Error class PlatformException extends RuntimeException {
    public PlatformException(@Mandatory String message) {
        super(message);
    }
}
