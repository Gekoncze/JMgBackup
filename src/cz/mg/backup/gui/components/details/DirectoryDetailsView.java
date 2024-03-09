package cz.mg.backup.gui.components.details;

import cz.mg.annotations.classes.Component;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.backup.entities.Directory;
import cz.mg.panel.Panel;

import javax.swing.*;

public @Component class DirectoryDetailsView extends Panel {
    public DirectoryDetailsView(@Mandatory Directory directory) {
        addVertical(new JLabel("Path: " + directory.getPath()));
        addVertical(new JLabel("Directory count: " + directory.getDirectories().count()));
        addVertical(new JLabel("File count: " + directory.getFiles().count()));
        addVertical(new JLabel("Errors:"));
        for (Exception error : directory.getErrors()) {
            addVertical(new JLabel("    " + error.getClass().getSimpleName() + ": " + error.getMessage()));
        }
    }
}
