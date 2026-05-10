package cz.mg.backup.gui.icons;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.requirement.Mandatory;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public @Service class IconCompositor {
    private static volatile @Service IconCompositor instance;

    public static @Service IconCompositor getInstance() {
        if (instance == null) {
            synchronized (Service.class) {
                if (instance == null) {
                    instance = new IconCompositor();
                }
            }
        }
        return instance;
    }

    private IconCompositor() {
    }

    public @Mandatory ImageIcon combine(
        @Mandatory ImageIcon mainIcon,
        @Mandatory ImageIcon secondaryIcon,
        @Mandatory Color color,
        int x,
        int y
    ) {
        BufferedImage secondaryCanvas = new BufferedImage(
            secondaryIcon.getIconWidth(),
            secondaryIcon.getIconHeight(),
            BufferedImage.TYPE_4BYTE_ABGR
        );

        Graphics2D sg = secondaryCanvas.createGraphics();
        sg.drawImage(secondaryIcon.getImage(), 0, 0, null);
        sg.setComposite(AlphaComposite.SrcAtop);
        sg.setColor(color);
        sg.drawRect(0, 0, secondaryIcon.getIconWidth(), secondaryIcon.getIconHeight());
        sg.dispose();

        BufferedImage mainCanvas = new BufferedImage(
            mainIcon.getIconWidth(),
            mainIcon.getIconHeight(),
            BufferedImage.TYPE_4BYTE_ABGR
        );

        Graphics2D mg = mainCanvas.createGraphics();
        mg.drawImage(mainIcon.getImage(), 0, 0, null);
        mg.drawImage(secondaryCanvas, x, y, null);
        mg.dispose();

        return new ImageIcon(mainCanvas);
    }
}
