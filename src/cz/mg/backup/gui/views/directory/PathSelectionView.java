package cz.mg.backup.gui.views.directory;

import cz.mg.annotations.classes.Component;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.annotations.requirement.Optional;
import cz.mg.backup.gui.MainWindow;
import cz.mg.backup.gui.actions.directory.LoadAction;
import cz.mg.backup.gui.actions.directory.OpenAction;
import cz.mg.backup.gui.actions.directory.ReloadAction;
import cz.mg.backup.gui.components.ActionButton;
import cz.mg.backup.gui.components.ActionPathField;
import cz.mg.panel.Panel;

import javax.swing.*;
import java.nio.file.Path;

public @Component class PathSelectionView extends Panel {
    private static final int MARGIN = 0;
    private static final int PADDING = 4;

    private final ActionPathField field;

    public PathSelectionView(@Mandatory MainWindow window, @Mandatory DirectoryTreeView parent) {
        super(MARGIN, PADDING);
        addHorizontal(new JLabel("Directory"));
        addHorizontal(field = new ActionPathField(new LoadAction(window, parent)), 1, 0);
        addHorizontal(new ActionButton(new OpenAction(window, parent)));
        addHorizontal(new ActionButton(new ReloadAction(window, parent)));
    }

    public @Optional Path getPath() {
        return field.getValue();
    }

    public void setPath(@Optional Path path) {
        field.setValue(path);
    }
}
