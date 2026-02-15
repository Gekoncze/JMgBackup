package cz.mg.backup.gui.components;

import cz.mg.annotations.classes.Component;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.annotations.requirement.Optional;
import cz.mg.backup.gui.actions.Action;

import java.nio.file.Path;

public @Component class ActionPathField extends ActionField<Path> {
    public ActionPathField(@Mandatory Action action) {
        super(action);
    }

    @Override
    protected @Optional Path textToValue(@Mandatory String text) {
        return text.isBlank() ? null : Path.of(text.trim());
    }

    @Override
    protected @Mandatory String valueToText(@Optional Path value) {
        return value == null ? "" : value.toString();
    }
}
