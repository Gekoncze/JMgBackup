package cz.mg.backup.gui.components;

import cz.mg.annotations.classes.Component;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.backup.gui.event.UserMouseClickListener;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.font.TextAttribute;
import java.net.URI;
import java.util.HashMap;

public @Component class UrlLabel extends JLabel {
    public UrlLabel(@Mandatory String text) {
        super(text);
        addMouseListener(new UserMouseClickListener(this::onClicked));
        setForeground(Color.BLUE);
        setFont(createFont(getFont()));
        setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    private @Mandatory Font createFont(@Mandatory Font originalFont) {
        Font font = new Font(originalFont.getName(), Font.PLAIN, originalFont.getSize());
        HashMap<TextAttribute, Object> attributes = new HashMap<>(font.getAttributes());
        attributes.put(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_ON);
        return font.deriveFont(attributes);
    }

    private void onClicked(@Mandatory MouseEvent event) {
        try {
            Desktop.getDesktop().browse(new URI(getText()));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
