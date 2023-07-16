package cz.mg.backup;

import cz.mg.annotations.classes.Static;
import cz.mg.annotations.requirement.Mandatory;

public @Static class Info {
    public static final @Mandatory String NAME = "JMgBackup";
    public static final @Mandatory String VERSION = Version.getInstance().toString();
    public static final @Mandatory String URL = "https://github.com/Gekoncze/JMgBackup";
}
