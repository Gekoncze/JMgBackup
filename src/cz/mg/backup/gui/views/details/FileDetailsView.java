package cz.mg.backup.gui.views.details;

import cz.mg.annotations.classes.Component;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.backup.entities.File;
import cz.mg.backup.gui.components.TextLabel;
import cz.mg.backup.gui.components.TitleLabel;
import cz.mg.panel.Panel;

import static cz.mg.backup.gui.common.Format.format;

public @Component class FileDetailsView extends Panel {
    public FileDetailsView(@Mandatory File file) {
        addFields(file);
        addProperties(file);
        addChecksum(file);
        addError(file);
    }

    private void addFields(@Mandatory File file) {
        addVertical(new TitleLabel(file.getPath().getFileName().toString()));
        addVertical(new TextLabel("Path: " + file.getPath()));
        addVertical(new TextLabel("Relative path: " + file.getRelativePath()));
    }

    private void addProperties(@Mandatory File file) {
        addVertical(new TextLabel("Size: " + format(file.getProperties().getSize()) + " bytes"));
        addVertical(new TextLabel("Created: " + format(file.getProperties().getCreated())));
        addVertical(new TextLabel("Modified: " + format(file.getProperties().getModified())));
    }

    private void addChecksum(@Mandatory File file) {
        if (file.getChecksum() != null) {
            addVertical(new TextLabel("Hash: " + file.getChecksum().getHash() + " (" + file.getChecksum().getAlgorithm() + ")"));
        }
    }

    private void addError(@Mandatory File file) {
        if (file.getError() != null) {
            addVertical(new TextLabel(file.getError().getClass().getSimpleName() + ": " + file.getError().getMessage()));
        }
    }
}
