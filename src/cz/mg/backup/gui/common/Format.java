package cz.mg.backup.gui.common;

import cz.mg.annotations.classes.Static;
import cz.mg.annotations.requirement.Mandatory;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public @Static class Format {
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd. MM. yyyy, HH:mm");

    public static @Mandatory String format(int value) {
        return String.format("%,d", value);
    }

    public static @Mandatory String format(long value) {
        return String.format("%,d", value);
    }

    public static @Mandatory String format(@Mandatory Instant date) {
        return DATE_FORMAT.format(date.atZone(ZoneId.systemDefault()));
    }
}
