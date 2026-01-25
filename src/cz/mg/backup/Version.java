package cz.mg.backup;

import cz.mg.annotations.classes.Component;
import cz.mg.annotations.requirement.Mandatory;

public @Component class Version {
    private static final @Mandatory Version INSTANCE = new Version(1, 19, 4);

    public static @Mandatory Version getInstance() {
        return INSTANCE;
    }

    private final int major;
    private final int minor;
    private final int patch;

    public Version(int major, int minor, int patch) {
        this.major = major;
        this.minor = minor;
        this.patch = patch;
    }

    @Override
    public String toString() {
        return major + "." + minor + "." + patch;
    }
}
