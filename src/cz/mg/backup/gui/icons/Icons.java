package cz.mg.backup.gui.icons;

import cz.mg.annotations.classes.Static;
import cz.mg.annotations.requirement.Mandatory;

import javax.swing.*;

public @Static class Icons {
    // tree view icons
    public static final Icon DIRECTORY_ICON = read("directory.png");
    public static final Icon DIRECTORY_ERROR_ICON = read("directoryError.png");
    public static final Icon DIRECTORY_ERROR_NESTED_ICON = read("directoryErrorNested.png");
    public static final Icon FILE_ICON = read("file.png");
    public static final Icon FILE_ERROR_ICON = read("fileError.png");

    // menu icons 16 px
    public static final Icon STANDARD_EXIT_16 = read("lucide/log-out-16.png");
    public static final Icon STANDARD_HELP_16 = read("lucide/circle-help-16.png");
    public static final Icon STANDARD_OPEN_16 = read("lucide/folder-open-16.png");
    public static final Icon STANDARD_RELOAD_16 = read("lucide/refresh-cw-16.png");
    public static final Icon STANDARD_SETTINGS_16 = read("lucide/settings-16.png");

    // menu icons 20 px
    public static final Icon STANDARD_EXIT_20 = read("lucide/log-out-20.png");
    public static final Icon STANDARD_HELP_20 = read("lucide/circle-help-20.png");
    public static final Icon STANDARD_OPEN_20 = read("lucide/folder-open-20.png");
    public static final Icon STANDARD_RELOAD_20 = read("lucide/refresh-cw-20.png");
    public static final Icon STANDARD_SETTINGS_20 = read("lucide/settings-20.png");

    private static @Mandatory Icon read(@Mandatory String name) {
        return IconReader.getInstance().read(name);
    }
}
