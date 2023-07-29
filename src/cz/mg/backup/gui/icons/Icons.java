package cz.mg.backup.gui.icons;

import cz.mg.annotations.classes.Static;
import cz.mg.annotations.requirement.Mandatory;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.io.IOException;
import java.util.Objects;

public @Static class Icons {
    public static final Icon DIRECTORY_ICON = load("directory.png");
    public static final Icon FILE_ICON = load("file.png");

    private static @Mandatory Icon load(@Mandatory String name) {
        try {
            return new ImageIcon(
                ImageIO.read(
                    Objects.requireNonNull(
                        Icons.class.getResourceAsStream(name)
                    )
                )
            );
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
