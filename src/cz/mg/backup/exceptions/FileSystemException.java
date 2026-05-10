package cz.mg.backup.exceptions;

import cz.mg.annotations.classes.Error;
import cz.mg.annotations.requirement.Mandatory;

public @Error class FileSystemException extends RuntimeException implements CategorizedException {
    public FileSystemException(@Mandatory String message) {
        super(message);
    }

    public FileSystemException(@Mandatory String message, @Mandatory Exception e) {
        super(message, e);
    }

    @Override
    public Category getCategory() {
        return Category.ERROR;
    }
}
