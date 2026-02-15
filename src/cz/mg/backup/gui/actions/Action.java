package cz.mg.backup.gui.actions;

import cz.mg.annotations.classes.Component;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.annotations.requirement.Optional;

import javax.swing.*;

public @Component interface Action {
    @Mandatory String getName();
    @Optional Character getMnemonic();
    @Optional KeyStroke getShortcut();
    @Optional Icon getSmallIcon();
    @Optional Icon getLargeIcon();
    void run();
}
