package cz.mg.backup.errors;

import cz.mg.annotations.classes.Error;

public @Error class CancelException extends RuntimeException {
    public CancelException() {
        super("User requested cancel of current operation.");
    }
}
