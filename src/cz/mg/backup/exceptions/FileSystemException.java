package cz.mg.backup.exceptions;

import cz.mg.annotations.classes.Error;
import cz.mg.annotations.requirement.Mandatory;

public @Error class FileSystemException extends RuntimeException {
    public FileSystemException(@Mandatory Exception e) {
        super(e.getMessage(), e);
    }

    public FileSystemException(@Mandatory String message) {
        super(message);
    }

    public FileSystemException(@Mandatory String message, @Mandatory Exception e) {
        super(message, e);
    }
}
