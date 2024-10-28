package cz.mg.backup.exceptions;

import cz.mg.annotations.classes.Error;
import cz.mg.annotations.requirement.Mandatory;

public @Error class StorageException extends RuntimeException {
    public StorageException(@Mandatory Exception e) {
        super(e.getMessage(), e);
    }

    public StorageException(@Mandatory String message, @Mandatory Exception e) {
        super(message, e);
    }
}
