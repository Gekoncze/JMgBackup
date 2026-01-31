package cz.mg.backup.gui.common;

import cz.mg.annotations.classes.Static;
import cz.mg.annotations.requirement.Mandatory;

import java.text.SimpleDateFormat;
import java.util.Date;

public @Static class Format {
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd. MM. yyyy HH:mm");

    public static @Mandatory String format(int value) {
        return String.format("%,d", value);
    }

    public static @Mandatory String format(long value) {
        return String.format("%,d", value);
    }

    public static @Mandatory String format(@Mandatory Date date) {
        return DATE_FORMAT.format(date);
    }
}
