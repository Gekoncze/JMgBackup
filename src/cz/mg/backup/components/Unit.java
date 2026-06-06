package cz.mg.backup.components;

import cz.mg.annotations.requirement.Mandatory;

public enum Unit {
    BYTE("Byte", "B");

    private final @Mandatory String longForm;
    private final @Mandatory String shortForm;

    Unit(@Mandatory String longForm, @Mandatory String shortForm) {
        this.longForm = longForm;
        this.shortForm = shortForm;
    }

    public @Mandatory String getLongForm() {
        return longForm;
    }

    public @Mandatory String getShortForm() {
        return shortForm;
    }
}
