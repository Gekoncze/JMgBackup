package cz.mg.backup;

import cz.mg.annotations.classes.Component;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.annotations.requirement.Optional;

public @Component class Version {
    private static final @Mandatory Version INSTANCE = new Version(1, 25, 0, null);

    public static @Mandatory Version getInstance() {
        return INSTANCE;
    }

    private final int major;
    private final int minor;
    private final int patch;
    private final @Optional String note;

    public Version(int major, int minor, int patch, @Optional String note) {
        this.major = major;
        this.minor = minor;
        this.patch = patch;
        this.note = note;
    }

    @Override
    public String toString() {
        String suffix = note == null ? "" : " (" + note + ")";
        return major + "." + minor + "." + patch + suffix;
    }
}
