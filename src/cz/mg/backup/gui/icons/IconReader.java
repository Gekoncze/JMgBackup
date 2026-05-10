package cz.mg.backup.gui.icons;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.backup.exceptions.FileSystemException;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
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

    public @Mandatory ImageIcon read(@Mandatory String name, @Mandatory Class<?> location) {
        try {
            InputStream stream = location.getResourceAsStream(name);
            if (stream == null) {
                throw new FileSystemException("Could not find icon '" + name + "'.");
            }
            BufferedImage image = ImageIO.read(stream);
            if (image == null) {
                throw new FileSystemException("Invalid icon file '" + name + "'.");
            }
            return new ImageIcon(image);
        } catch (IOException e) {
            throw new FileSystemException("Could not load icon '" + name + "'.", e);
        }
    }
}
