package cz.mg.backup.gui.views.details;

import cz.mg.annotations.classes.Component;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.backup.entities.File;
import cz.mg.backup.gui.components.PlainLabel;
import cz.mg.backup.gui.components.TitleLabel;
import cz.mg.panel.Panel;

import java.text.SimpleDateFormat;
import java.util.Date;

public @Component class FileDetailsView extends Panel {
    private static final SimpleDateFormat FORMAT = new SimpleDateFormat("dd. MM. yyyy HH:mm");

    public FileDetailsView(@Mandatory File file) {
        addVertical(new TitleLabel(file.getPath().getFileName().toString()));
        addVertical(new PlainLabel("Path: " + file.getPath()));
        addVertical(new PlainLabel("Size: " + String.format("%,d", file.getProperties().getSize()) + " bytes"));
        addVertical(new PlainLabel("Created: " + formatDate(file.getProperties().getCreated())));
        addVertical(new PlainLabel("Modified: " + formatDate(file.getProperties().getModified())));

        if (file.getChecksum() != null) {
            addVertical(new PlainLabel(
                "Hash: " + file.getChecksum().getHash() + " (" + file.getChecksum().getAlgorithm() + ")"
            ));
        }

        if (!file.getErrors().isEmpty()) {
            addVertical(new PlainLabel("Errors:"));
            for (Exception error : file.getErrors()) {
                addVertical(new PlainLabel("    " + error.getClass().getSimpleName() + ": " + error.getMessage()));
            }
        }
    }

    private @Mandatory String formatDate(@Mandatory Date date) {
        return FORMAT.format(date);
    }
}
