package cz.mg.backup.gui.components;

import cz.mg.annotations.classes.Component;
import cz.mg.annotations.requirement.Mandatory;

import javax.swing.*;
import java.awt.*;

public @Component class TextLabel extends JTextField {
    public TextLabel(@Mandatory String text) {
        super(text);
        setEditable(false);
        setOpaque(false);
        setBorder(null);
        setBackground(null);
        Font font = getFont();
        setFont(new Font(
            font.getName(),
            Font.PLAIN,
            font.getSize()
        ));
    }
}
