package cz.mg.backup.gui.dialogs;

import cz.mg.annotations.classes.Component;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.backup.entities.File;
import cz.mg.backup.gui.components.TextButton;
import cz.mg.backup.gui.components.TitleLabel;
import cz.mg.collections.components.StringJoiner;
import cz.mg.collections.list.List;
import cz.mg.panel.Panel;
import cz.mg.panel.settings.Alignment;

import javax.swing.*;

import static cz.mg.backup.gui.common.Format.*;

public @Component class CopyMissingFilesDialog extends Dialog {
    private static final int MARGIN = 8;
    private static final int PADDING = 8;
    private static final int DEFAULT_WIDTH = 640;
    private static final int DEFAULT_HEIGHT = 480;

    private boolean accepted = false;

    private CopyMissingFilesDialog(@Mandatory JFrame window, @Mandatory List<File> files) {
        super(window);
        setTitle("Copy missing files");

        Panel panel = new Panel(MARGIN, PADDING);

        TitleLabel messageLabel = new TitleLabel(
            files.count() == 1
                ? "One file of size " + format(size(files)) + " will be copied."
                : "Total of " + format(files.count()) + " files of total size " + format(size(files)) + " will be copied."
        );
        panel.addVertical(messageLabel);

        JTextArea textArea = new JTextArea(
            new StringJoiner<>(files)
                .withDelimiter("\n")
                .withConverter(file -> file.getPath().toString())
                .join()
        );
        textArea.setEditable(false);
        panel.addVertical(new JScrollPane(textArea), 1, 1);

        Panel buttonsPanel = new Panel(0, PADDING, Alignment.RIGHT);
        buttonsPanel.addHorizontal(new TextButton("Cancel", this::cancel));
        buttonsPanel.addHorizontal(new TextButton("Ok", this::accept));
        panel.addVertical(buttonsPanel, 1, 0);

        getContentPane().add(panel);

        setSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);
        center();
    }

    private void cancel() {
        setAccepted(false);
        dispose();
    }

    private void accept() {
        setAccepted(true);
        dispose();
    }

    private boolean isAccepted() {
        return accepted;
    }

    public void setAccepted(boolean accepted) {
        this.accepted = accepted;
    }

    private long size(@Mandatory List<File> files) {
        long size = 0;
        for (File file : files) {
            size += file.getProperties().getSize();
        }
        return size;
    }

    public static boolean show(@Mandatory JFrame window, @Mandatory List<File> files) {
        CopyMissingFilesDialog dialog = new CopyMissingFilesDialog(window, files);
        dialog.setVisible(true);
        return dialog.isAccepted();
    }
}
