package cz.mg.backup.exceptions;

import cz.mg.annotations.classes.Error;
import cz.mg.annotations.requirement.Mandatory;

public @Error class NestedException extends CompareException {
    public NestedException(@Mandatory String message) {
        super(message);
    }
}
