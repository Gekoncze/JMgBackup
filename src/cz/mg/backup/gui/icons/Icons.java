package cz.mg.backup.gui.icons;

import cz.mg.annotations.classes.Static;
import cz.mg.annotations.requirement.Mandatory;

import javax.swing.*;

public @Static class Icons {
    private static final IconReader READER = IconReader.getInstance();

    // node icons
    public static final Icon DIRECTORY_ICON = READER.read("directory.png");
    public static final Icon DIRECTORY_ERROR_ICON = READER.read("directoryError.png");
    public static final Icon DIRECTORY_ERROR_NESTED_ICON = READER.read("directoryErrorNested.png");
    public static final Icon FILE_ICON = READER.read("file.png");
    public static final Icon FILE_ERROR_ICON = READER.read("fileError.png");

    // standard icons 16 px
    public static final Icon STANDARD_EXIT_16 = READER.read("lucide/log-out-16.png");
    public static final Icon STANDARD_HELP_16 = READER.read("lucide/circle-help-16.png");
    public static final Icon STANDARD_OPEN_16 = READER.read("lucide/folder-open-16.png");
    public static final Icon STANDARD_RELOAD_16 = READER.read("lucide/refresh-cw-16.png");
    public static final Icon STANDARD_SETTINGS_16 = READER.read("lucide/settings-16.png");

    // standard icons 20 px
    public static final Icon STANDARD_EXIT_20 = READER.read("lucide/log-out-20.png");
    public static final Icon STANDARD_HELP_20 = READER.read("lucide/circle-help-20.png");
    public static final Icon STANDARD_OPEN_20 = READER.read("lucide/folder-open-20.png");
    public static final Icon STANDARD_RELOAD_20 = READER.read("lucide/refresh-cw-20.png");
    public static final Icon STANDARD_SETTINGS_20 = READER.read("lucide/settings-20.png");
}
