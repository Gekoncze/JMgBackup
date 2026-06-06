package cz.mg.backup.exceptions;

import cz.mg.annotations.classes.Error;
import cz.mg.annotations.requirement.Mandatory;

public @Error class CancelException extends RuntimeException implements CategorizedException {
    public CancelException() {
        super("User requested cancel of current operation.");
    }

    @Override
    public @Mandatory Category getCategory() {
        return Category.ERROR;
    }
}
