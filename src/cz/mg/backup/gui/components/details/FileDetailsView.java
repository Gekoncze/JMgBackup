package cz.mg.backup.gui.components.details;

import cz.mg.annotations.classes.Component;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.backup.entities.File;
import cz.mg.panel.Panel;

import javax.swing.*;

public @Component class FileDetailsView extends Panel {
    public FileDetailsView(@Mandatory File file) {
        addVertical(new JLabel("Path: " + file.getPath()));
        addVertical(new JLabel("Size: " + file.getSize()));
        addVertical(new JLabel("Hash: " + file.getHash()));
        addVertical(new JLabel("Errors:"));
        for (Exception error : file.getErrors()) {
            addVertical(new JLabel("    " + error.getClass().getSimpleName() + ": " + error.getMessage()));
        }
    }
}
