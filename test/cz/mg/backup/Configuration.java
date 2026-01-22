package cz.mg.backup;

import cz.mg.annotations.classes.Static;
import cz.mg.annotations.requirement.Mandatory;

import java.nio.file.Path;

public @Static class Configuration {
    public static @Mandatory Path getRoot(@Mandatory Class<?> testClass) {
        return Path.of("test", "cz", "mg", "backup", "test", testClass.getSimpleName().toLowerCase());
    }

    public static final Path FLYING_AKI_PATH = Path.of("test", "cz", "mg", "backup", "test", "FlyingAki.png");
    public static final String FLYING_AKI_HASH = "357e9abbbe50922c6c0b31cb8f4371add40deaf39924e54acdbc691b7975f576";
}
