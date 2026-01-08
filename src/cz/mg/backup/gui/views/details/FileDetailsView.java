package cz.mg.backup.gui.views.details;

import cz.mg.annotations.classes.Component;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.backup.entities.File;
import cz.mg.backup.gui.components.TextLabel;
import cz.mg.backup.gui.components.TitleLabel;
import cz.mg.panel.Panel;

import java.text.SimpleDateFormat;
import java.util.Date;

public @Component class FileDetailsView extends Panel {
    private static final SimpleDateFormat FORMAT = new SimpleDateFormat("dd. MM. yyyy HH:mm");

    public FileDetailsView(@Mandatory File file) {
        addVertical(new TitleLabel(file.getPath().getFileName().toString()));
        addVertical(new TextLabel("Path: " + file.getPath()));
        addVertical(new TextLabel("Relative path: " + file.getRelativePath()));
        addVertical(new TextLabel("Size: " + String.format("%,d", file.getProperties().getSize()) + " bytes"));
        addVertical(new TextLabel("Created: " + formatDate(file.getProperties().getCreated())));
        addVertical(new TextLabel("Modified: " + formatDate(file.getProperties().getModified())));

        if (file.getChecksum() != null) {
            addVertical(new TextLabel(
                "Hash: " + file.getChecksum().getHash() + " (" + file.getChecksum().getAlgorithm() + ")"
            ));
        }

        if (!file.getErrors().isEmpty()) {
            addVertical(new TextLabel("Errors:"));
            for (Exception error : file.getErrors()) {
                addVertical(new TextLabel("    " + error.getClass().getSimpleName() + ": " + error.getMessage()));
            }
        }
    }

    private @Mandatory String formatDate(@Mandatory Date date) {
        return FORMAT.format(date);
    }
}
