package cz.mg.backup.gui.dialogs;

import cz.mg.annotations.classes.Component;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.annotations.requirement.Optional;

import javax.swing.*;
import java.io.File;
import java.nio.file.Path;

public @Component class OpenDirectoryDialog extends JFileChooser {
    private OpenDirectoryDialog() {
        setDialogType(JFileChooser.OPEN_DIALOG);
        setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
    }

    public static @Optional Path show(@Mandatory java.awt.Component window) {
        OpenDirectoryDialog dialog = new OpenDirectoryDialog();
        int result = dialog.showOpenDialog(window);
        File file = dialog.getSelectedFile();
        if (file != null && result == JFileChooser.APPROVE_OPTION) {
            return file.toPath();
        } else {
            return null;
        }
    }
}
