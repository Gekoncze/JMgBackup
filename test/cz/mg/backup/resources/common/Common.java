package cz.mg.backup.resources.common;

import cz.mg.annotations.classes.Static;
import cz.mg.backup.Configuration;

import java.nio.file.Path;

public @Static class Common {
    public static final Path PATH = Configuration.RESOURCES_ROOT.resolve("common");
    public static final Path FLYING_AKI_PATH = PATH.resolve("FlyingAki.png");
    public static final String FLYING_AKI_HASH = "357e9abbbe50922c6c0b31cb8f4371add40deaf39924e54acdbc691b7975f576";
}
