package cz.mg.backup.gui.views.details;

import cz.mg.annotations.classes.Component;
import cz.mg.annotations.requirement.Optional;
import cz.mg.backup.entities.Directory;
import cz.mg.backup.entities.File;
import cz.mg.backup.entities.Node;

import javax.swing.*;
import java.nio.file.Path;

public @Component class DetailsView extends JScrollPane {
    private @Optional Node node;

    public DetailsView() {
    }

    public @Optional Node getNode() {
        return node;
    }

    public void setNode(@Optional Node node) {
        this.node = node;
        refresh();
    }

    public @Optional Path getPath() {
        return node != null ? node.getPath() : null;
    }

    private void refresh() {
        getViewport().removeAll();

        if (node != null) {
            if (node instanceof File file) {
                getViewport().add(new FileDetailsView(file), 1, 1);
            }

            if (node instanceof Directory directory) {
                getViewport().add(new DirectoryDetailsView(directory), 1, 1);
            }
        }

        repaint();
    }
}
