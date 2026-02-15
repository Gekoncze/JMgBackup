package cz.mg.backup.gui.icons;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.backup.exceptions.FileSystemException;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.io.IOException;
import java.util.Objects;

public @Service class IconReader {
    private static volatile @Service IconReader instance;

    public static @Service IconReader getInstance() {
        if (instance == null) {
            synchronized (Service.class) {
                if (instance == null) {
                    instance = new IconReader();
                }
            }
        }
        return instance;
    }

    private IconReader() {
    }

    public @Mandatory Icon read(@Mandatory String name) {
        try {
            return new ImageIcon(
                ImageIO.read(
                    Objects.requireNonNull(
                        Icons.class.getResourceAsStream(name),
                        "Could not find icon '" + name + "'."
                    )
                )
            );
        } catch (IOException e) {
            throw new FileSystemException("Could not load icon '" + name + "'.", e);
        }
    }
}
