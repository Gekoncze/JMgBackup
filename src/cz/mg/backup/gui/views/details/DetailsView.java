package cz.mg.backup.gui.views.details;

import cz.mg.annotations.classes.Component;
import cz.mg.annotations.requirement.Optional;
import cz.mg.backup.entities.Directory;
import cz.mg.backup.entities.File;
import cz.mg.backup.entities.Node;

import javax.swing.*;

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
