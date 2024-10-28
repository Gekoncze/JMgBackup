package cz.mg.backup.gui.components;

import cz.mg.annotations.classes.Component;
import cz.mg.annotations.requirement.Mandatory;

import javax.swing.*;
import java.awt.*;

public @Component class PlainLabel extends JLabel {
    public PlainLabel(@Mandatory String text) {
        super(text);
        Font font = getFont();
        setFont(new Font(
            font.getName(),
            Font.PLAIN,
            font.getSize()
        ));
    }
}
