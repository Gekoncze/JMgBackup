package cz.mg.backup.exceptions;

import cz.mg.annotations.classes.Error;
import cz.mg.annotations.requirement.Mandatory;

public @Error class PropagatedException extends RuntimeException {
    public PropagatedException(@Mandatory Exception cause) {
        super("Child node has an error.", cause);
    }
}
