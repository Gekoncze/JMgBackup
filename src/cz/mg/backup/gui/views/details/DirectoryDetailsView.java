package cz.mg.backup.gui.views.details;

import cz.mg.annotations.classes.Component;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.backup.entities.Directory;
import cz.mg.backup.gui.components.PlainLabel;
import cz.mg.backup.gui.components.TitleLabel;
import cz.mg.panel.Panel;

public @Component class DirectoryDetailsView extends Panel {
    public DirectoryDetailsView(@Mandatory Directory directory) {
        addVertical(new TitleLabel(directory.getPath().getFileName().toString()));
        addVertical(new PlainLabel("Path: " + directory.getPath()));
        addVertical(new PlainLabel("Directory count: " + directory.getDirectories().count()
            + " (total " + directory.getProperties().getTotalDirectoryCount() + ")"));
        addVertical(new PlainLabel("File count: " + directory.getFiles().count()
            + " (total " + directory.getProperties().getTotalFileCount() + ")"));
        addVertical(new PlainLabel("Size: " + String.format("%,d", directory.getProperties().getTotalSize()) + " bytes"));

        if (!directory.getErrors().isEmpty()) {
            addVertical(new PlainLabel("Errors:"));
            for (Exception error : directory.getErrors()) {
                addVertical(new PlainLabel("    " + error.getClass().getSimpleName() + ": " + error.getMessage()));
            }
        }
    }
}
