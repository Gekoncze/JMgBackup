package cz.mg.backup.gui.icons;

import cz.mg.annotations.classes.Test;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.backup.gui.resources.Resources;
import cz.mg.backup.gui.test.Colors;

import javax.swing.*;
import java.awt.*;

public @Test class IconCompositorTest {
    public static void main(String[] args) {
        System.out.print("Running " + IconCompositorTest.class.getSimpleName() + " ... ");

        IconCompositorTest test = new IconCompositorTest();
        test.testCombine();

        System.out.println("OK");
    }

    private final @Mandatory IconReader reader = IconReader.getInstance();
    private final @Mandatory IconCompositor compositor = IconCompositor.getInstance();

    private void testCombine() {
        ImageIcon mainIcon = reader.read("main.png", Resources.class);
        ImageIcon secondaryIcon = reader.read("secondary.png", Resources.class);
        Color color = new Color(0, 0, 255);

        ImageIcon icon = compositor.combine(mainIcon, secondaryIcon, color, 8, 8);

        Colors.verify(icon, 0, 0, new Color(0, 0, 0, 0));
        Colors.verify(icon, 2, 2, new Color(255, 0, 0, 255));
        Colors.verify(icon, 8, 8, new Color(0, 0, 255, 255));
        Colors.verify(icon, 9, 9, new Color(0, 0, 255, 255));
        Colors.verify(icon, 15, 8, new Color(0, 0, 255, 255));
        Colors.verify(icon, 8, 15, new Color(0, 0, 255, 255));
        Colors.verify(icon, 15, 15, new Color(0, 0, 0, 0));
    }
}
