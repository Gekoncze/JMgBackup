package cz.mg.backup.gui.components;

import cz.mg.annotations.classes.Component;
import cz.mg.annotations.requirement.Mandatory;

import javax.swing.*;

public @Component class TitleLabel extends JLabel {
    public TitleLabel(@Mandatory String text) {
        super(text);
    }
}
