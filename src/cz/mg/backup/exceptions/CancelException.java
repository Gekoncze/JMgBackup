package cz.mg.backup.exceptions;

import cz.mg.annotations.classes.Error;

public @Error class CancelException extends RuntimeException implements CategorizedException {
    public CancelException() {
        super("User requested cancel of current operation.");
    }

    @Override
    public Category getCategory() {
        return Category.ERROR;
    }
}
