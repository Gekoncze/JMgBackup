package cz.mg.backup.exceptions;

import cz.mg.annotations.classes.Error;
import cz.mg.annotations.requirement.Mandatory;

public @Error class NestedException extends CompareException {
    private final @Mandatory Category category;

    public NestedException(@Mandatory Category category, @Mandatory String message) {
        super(message);
        this.category = category;
    }

    @Override
    public @Mandatory Category getCategory() {
        return category;
    }
}
