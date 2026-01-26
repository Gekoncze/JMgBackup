package cz.mg.backup.gui.dialogs;

import cz.mg.annotations.classes.Component;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.backup.components.Progress;
import cz.mg.backup.components.Status;
import cz.mg.backup.components.Task;
import cz.mg.backup.gui.components.ProgressBar;
import cz.mg.backup.gui.components.TextButton;
import cz.mg.backup.gui.event.UserEscapeKeyPressListener;
import cz.mg.backup.gui.event.UserWindowClosedListener;
import cz.mg.backup.gui.event.UserWindowClosingListener;
import cz.mg.collections.list.List;
import cz.mg.collections.list.ListItem;
import cz.mg.panel.Panel;
import cz.mg.panel.settings.Alignment;
import cz.mg.panel.settings.Fill;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.util.function.Consumer;

public @Component class ProgressDialog extends Dialog {
    private static final int MARGIN = 8;
    private static final int PADDING = 8;
    private static final int REFRESH_DELAY = 100; // milliseconds
    private static final int DEFAULT_WIDTH = 512;
    private static final int DEFAULT_HEIGHT = 192;

    private final @Mandatory Task<?> task;
    private final @Mandatory Timer timer;
    private final @Mandatory Panel progressPanel;
    private final @Mandatory List<ProgressBar> progressBars = new List<>();

    private ProgressDialog(
        @Mandatory JFrame window,
        @Mandatory String title,
        @Mandatory Task<?> task
    ) {
        super(window);
        setTitle(title);
        setSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);

        Panel panel = new Panel(MARGIN, PADDING);

        progressPanel = new Panel(0, PADDING);

        JScrollPane progressScrollPane = new JScrollPane(progressPanel);
        progressScrollPane.setBorder(null);
        panel.addVertical(progressScrollPane, 1, 1);

        panel.addVertical(new TextButton("Cancel", this::cancel), 0, 0, Alignment.MIDDLE, Fill.NONE);

        getContentPane().add(panel);
        setLocationRelativeTo(null);

        addWindowListener(new UserWindowClosingListener(this::cancel));
        addWindowListener(new UserWindowClosedListener(this::closed));
        addKeyListenerRecursive(this, new UserEscapeKeyPressListener(this::cancel));

        this.task = task;
        this.timer = new Timer(REFRESH_DELAY, this::refresh);

        refreshProgress();
    }

    private void start() {
        task.start();
        timer.start();
    }

    private void cancel() {
        task.cancel();
        task.join();
    }

    private void closed() {
        timer.stop();
    }

    private void rethrow() {
        if (task.getException() != null) {
            throw task.getException();
        }
    }

    private void refresh(@Mandatory ActionEvent event) {
        try {
            refreshProgress();

            if (task.getStatus() != Status.RUNNING) {
                SwingUtilities.invokeLater(this::dispose);
            }
        } catch (Exception e) {
            timer.stop();
        }
    }

    private void refreshProgress() {
        List<ProgressBar> existingProgressBars = new List<>(progressBars);
        ListItem<ProgressBar> existingProgressBar = existingProgressBars.getFirstItem();

        progressPanel.clear();
        progressBars.clear();

        Progress progress = task.getProgress();

        while (progress != null) {
            ProgressBar progressBar = existingProgressBar != null
                ? existingProgressBar.get().update(progress)
                : new ProgressBar(progress);

            progressPanel.addVertical(progressBar, 1, 0);
            progressBars.addLast(progressBar);
            progress = progress.getNext();

            if (existingProgressBar != null) {
                existingProgressBar = existingProgressBar.getNextItem();
            }
        }
    }

    /**
     * Runs given runnable in a separate thread with a progress dialog showing progress.
     * Returns true if the runnable completed successfully, false otherwise.
     */
    public static boolean run(
        @Mandatory JFrame window,
        @Mandatory String title,
        @Mandatory Consumer<Progress> runnable
    ) {
        Task<?> task = new Task<>(runnable);
        ProgressDialog dialog = new ProgressDialog(window, title, task);
        dialog.start();
        dialog.setVisible(true);
        dialog.rethrow();
        return task.getStatus() == Status.COMPLETED;
    }
}
