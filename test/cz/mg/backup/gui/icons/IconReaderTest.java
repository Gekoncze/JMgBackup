package cz.mg.backup.gui.icons;

import cz.mg.annotations.classes.Test;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.backup.exceptions.FileSystemException;
import cz.mg.backup.gui.resources.Resources;
import cz.mg.backup.gui.test.Colors;
import cz.mg.test.Assertions;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public @Test class IconReaderTest {
    public static void main(String[] args) {
        System.out.print("Running " + IconReaderTest.class.getSimpleName() + " ... ");

        IconReaderTest test = new IconReaderTest();
        test.testLoadMissing();
        test.testLoadInvalid();
        test.testLoad();

        System.out.println("OK");
    }

    private final @Mandatory IconReader reader = IconReader.getInstance();

    private void testLoadMissing() {
        Assertions.assertThatCode(() -> reader.read("missing", Resources.class))
            .withMessage("Missing icon should cause expected exception.")
            .throwsException(FileSystemException.class);
    }

    private void testLoadInvalid() {
        Assertions.assertThatCode(() -> reader.read("text.txt", Resources.class))
            .withMessage("Invalid icon should cause expected exception.")
            .throwsException(FileSystemException.class);
    }

    private void testLoad() {
        ImageIcon icon = reader.read("main.png", Resources.class);
        Colors.verify(icon, 0, 0, new Color(0, 0, 0, 0));
        Colors.verify(icon, 2, 2, new Color(255, 0, 0, 255));
    }
}
