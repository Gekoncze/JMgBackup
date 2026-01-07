package cz.mg.backup.exceptions;

import cz.mg.annotations.classes.Error;
import cz.mg.annotations.requirement.Mandatory;

public @Error class MismatchException extends CompareException {
    public MismatchException(@Mandatory String message) {
        super(message);
    }
}
