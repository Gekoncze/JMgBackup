package cz.mg.backup;

import cz.mg.annotations.classes.Static;
import cz.mg.annotations.requirement.Mandatory;

import java.nio.file.Path;

public @Static class Configuration {
    public static final @Mandatory Path RESOURCES_ROOT = Path.of("test", "cz", "mg", "backup", "resources");

    public static @Mandatory Path getRoot(@Mandatory Class<?> testClass) {
        return RESOURCES_ROOT.resolve(testClass.getSimpleName().toLowerCase());
    }
}
