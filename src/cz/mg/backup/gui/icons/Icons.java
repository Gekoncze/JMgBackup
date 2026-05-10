package cz.mg.backup.gui.icons;

import cz.mg.annotations.classes.Static;

import javax.swing.*;
import java.awt.*;

public @Static class Icons {
    private static final IconReader READER = IconReader.getInstance();
    private static final IconCompositor COMPOSITOR = IconCompositor.getInstance();

    // node icons
    public static final ImageIcon DIRECTORY = READER.read("node/directory.png", Icons.class);
    public static final ImageIcon FILE = READER.read("node/file.png", Icons.class);
    private static final ImageIcon EXCEPTION = READER.read("node/exception.png", Icons.class);
    private static final ImageIcon NESTED = READER.read("node/nested.png", Icons.class);

    public static final Icon DIRECTORY_ERROR = COMPOSITOR.combine(DIRECTORY, EXCEPTION, Color.RED, 13, 13);
    public static final Icon DIRECTORY_ERROR_NESTED = COMPOSITOR.combine(DIRECTORY, NESTED, Color.RED, 13, 13);
    public static final Icon FILE_ERROR = COMPOSITOR.combine(FILE, EXCEPTION, Color.RED, 13, 13);

    // standard icons 16 px
    public static final Icon STANDARD_EXIT_16 = READER.read("lucide/log-out-16.png", Icons.class);
    public static final Icon STANDARD_HELP_16 = READER.read("lucide/circle-help-16.png", Icons.class);
    public static final Icon STANDARD_OPEN_16 = READER.read("lucide/folder-open-16.png", Icons.class);
    public static final Icon STANDARD_RELOAD_16 = READER.read("lucide/refresh-cw-16.png", Icons.class);
    public static final Icon STANDARD_SETTINGS_16 = READER.read("lucide/settings-16.png", Icons.class);

    // standard icons 20 px
    public static final Icon STANDARD_EXIT_20 = READER.read("lucide/log-out-20.png", Icons.class);
    public static final Icon STANDARD_HELP_20 = READER.read("lucide/circle-help-20.png", Icons.class);
    public static final Icon STANDARD_OPEN_20 = READER.read("lucide/folder-open-20.png", Icons.class);
    public static final Icon STANDARD_RELOAD_20 = READER.read("lucide/refresh-cw-20.png", Icons.class);
    public static final Icon STANDARD_SETTINGS_20 = READER.read("lucide/settings-20.png", Icons.class);
}
