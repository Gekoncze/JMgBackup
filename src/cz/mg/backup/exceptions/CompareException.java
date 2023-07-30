package cz.mg.backup.exceptions;

import cz.mg.annotations.classes.Error;
import cz.mg.annotations.requirement.Mandatory;

public @Error class CompareException extends RuntimeException {
    public CompareException(@Mandatory String message) {
        super(message);
    }

    protected CompareException(String message, Throwable cause) {
        super(message, cause);
    }
}
