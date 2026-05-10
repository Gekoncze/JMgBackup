package cz.mg.backup.exceptions;

import cz.mg.annotations.classes.Error;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.backup.entities.File;
import cz.mg.collections.list.List;

public @Error class DuplicateException extends RuntimeException implements CategorizedException {
    private final @Mandatory List<File> suspects;

    public DuplicateException(@Mandatory List<File> suspects) {
        super("Possible duplicate" + (suspects.count() > 1 ? "s" : "") + " found!");
        this.suspects = suspects;
    }

    public @Mandatory List<File> getSuspects() {
        return suspects;
    }

    @Override
    public Category getCategory() {
        return Category.WARNING;
    }
}
