package cz.mg.backup.exceptions;

import cz.mg.annotations.classes.Error;
import cz.mg.annotations.requirement.Mandatory;

public @Error class CompareException extends RuntimeException implements CategorizedException {
    public CompareException(@Mandatory String message) {
        super(message);
    }

    @Override
    public @Mandatory Category getCategory() {
        return Category.PROBLEM;
    }
}
