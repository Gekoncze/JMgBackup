package cz.mg.backup.gui.test;

import cz.mg.annotations.classes.Static;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.test.Assertions;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public @Static class Colors {
    public static @Mandatory Color read(@Mandatory ImageIcon icon, int x, int y) {
        return new Color(((BufferedImage) icon.getImage()).getRGB(x, y), true);
    }

    public static boolean equals(@Mandatory Color a, @Mandatory Color b) {
        return a.getRed() == b.getRed()
            && a.getGreen() == b.getGreen()
            && a.getBlue() == b.getBlue()
            && a.getAlpha() == b.getAlpha();
    }

    public static @Mandatory String format(@Mandatory Color c) {
        return "(" + c.getRed() + ", " + c.getGreen() + ", " + c.getBlue() + ", " + c.getAlpha() + ")";
    }

    public static void verify(@Mandatory ImageIcon icon, int x, int y, @Mandatory Color color) {
        Assertions.assertThat(Colors.read(icon, x, y))
            .withEqualsFunction(Colors::equals)
            .withFormatFunction(Colors::format)
            .isEqualTo(color);
    }
}
