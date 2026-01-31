package cz.mg.backup.gui.views.details;

import cz.mg.annotations.classes.Component;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.backup.entities.Directory;
import cz.mg.backup.gui.components.TextLabel;
import cz.mg.backup.gui.components.TitleLabel;
import cz.mg.panel.Panel;

import static cz.mg.backup.gui.common.Format.format;

public @Component class DirectoryDetailsView extends Panel {
    public DirectoryDetailsView(@Mandatory Directory directory) {
        addFields(directory);
        addProperties(directory);
        addError(directory);
    }

    private void addFields(@Mandatory Directory directory) {
        addVertical(new TitleLabel(directory.getPath().getFileName().toString()));
        addVertical(new TextLabel("Path: " + directory.getPath()));
        addVertical(new TextLabel("Relative path: " + directory.getRelativePath()));
    }

    private void addProperties(@Mandatory Directory directory) {
        addVertical(new TextLabel(
            "Directory count: " + format(directory.getDirectories().count())
                + " (total " + format(directory.getProperties().getTotalDirectoryCount()) + ")"
        ));
        addVertical(new TextLabel(
            "File count: " + format(directory.getFiles().count())
                + " (total " + format(directory.getProperties().getTotalFileCount()) + ")"
        ));
        addVertical(new TextLabel("Size: " + format(directory.getProperties().getTotalSize()) + " bytes"));
    }

    private void addError(@Mandatory Directory directory) {
        if (directory.getError() != null) {
            addVertical(new TextLabel(directory.getError().getClass().getSimpleName() + ": " + directory.getError().getMessage()));
        }
    }
}
