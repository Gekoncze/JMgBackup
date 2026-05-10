package cz.mg.backup.exceptions;

import cz.mg.annotations.classes.Error;
import cz.mg.annotations.requirement.Mandatory;

public @Error class MismatchException extends CompareException implements CategorizedException {
    public MismatchException(@Mandatory String message) {
        super(message);
    }

    @Override
    public Category getCategory() {
        return Category.PROBLEM;
    }
}
