package cz.mg.backup.gui.components;

import cz.mg.annotations.classes.Component;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.backup.components.Progress;
import cz.mg.panel.Panel;
import cz.mg.panel.settings.Alignment;
import cz.mg.panel.settings.Fill;

import javax.swing.*;

public @Component class ProgressBar extends Panel {
    private static final int PADDING = 8;

    private final @Mandatory JLabel descriptionLabel;
    private final @Mandatory JProgressBar progressBar;

    public ProgressBar(@Mandatory Progress progress) {
        super(0, PADDING);

        descriptionLabel = new JLabel();
        addVertical(descriptionLabel, 1, 0, Alignment.MIDDLE, Fill.BOTH);

        progressBar = new JProgressBar();
        addVertical(progressBar, 1, 0, Alignment.MIDDLE, Fill.BOTH);

        update(progress);
    }

    public @Mandatory ProgressBar update(@Mandatory Progress progress) {
        Double percent = progress.percent();

        String progressBarText = percent == null
            ? "" + progress.getValue()
            : progress.getValue() + " / " + progress.getLimit();

        progressBar.setIndeterminate(percent == null);
        progressBar.setMinimum(0);
        progressBar.setMaximum(100);
        progressBar.setValue(percent == null ? 0 : ((int) (double) percent));
        progressBar.setString(progressBarText);
        progressBar.setStringPainted(percent != null);

        descriptionLabel.setText(progress.getDescription());

        return this;
    }
}
