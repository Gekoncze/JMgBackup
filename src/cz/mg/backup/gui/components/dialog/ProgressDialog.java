package cz.mg.backup.gui.components.dialog;

import cz.mg.annotations.classes.Component;
import cz.mg.annotations.classes.Service;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.backup.components.Status;
import cz.mg.backup.components.Task;
import cz.mg.backup.gui.MainWindow;
import cz.mg.backup.gui.event.UserEscapeKeyPressListener;
import cz.mg.backup.gui.event.UserWindowClosedListener;
import cz.mg.backup.gui.event.UserWindowClosingListener;
import cz.mg.backup.gui.services.ButtonFactory;
import cz.mg.panel.Panel;
import cz.mg.panel.settings.Alignment;
import cz.mg.panel.settings.Fill;

import javax.swing.*;
import java.awt.event.ActionEvent;

public @Component class ProgressDialog extends Dialog {
    private static final int MARGIN = 8;
    private static final int PADDING = 8;
    private static final int REFRESH_DELAY = 25; // milliseconds

    private final @Service ButtonFactory buttonFactory = ButtonFactory.getInstance();

    private final @Mandatory Task task;
    private final @Mandatory Timer timer;

    private ProgressDialog(@Mandatory MainWindow window, @Mandatory String title, @Mandatory Runnable runnable) {
        super(window);
        setTitle(title);

        Panel panel = new Panel(MARGIN, PADDING);
        panel.addVertical(new JLabel("Task processing in progress ..."));
        panel.addVertical(buttonFactory.create("Cancel", this::cancel), 0, 0, Alignment.MIDDLE, Fill.NONE);

        getContentPane().add(panel);
        pack();
        setLocationRelativeTo(null);

        addWindowListener(new UserWindowClosingListener(this::cancel));
        addWindowListener(new UserWindowClosedListener(this::closed));
        addKeyListenerRecursive(this, new UserEscapeKeyPressListener(this::cancel));

        task = new Task(runnable);
        timer = new Timer(REFRESH_DELAY, this::refresh);
    }

    public void start() {
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
            // TODO - refresh progress bar(s)

            if (task.getStatus() != Status.RUNNING) {
                SwingUtilities.invokeLater(this::dispose);
            }
        } catch (Exception e) {
            timer.stop();
        }
    }

    public static void show(@Mandatory MainWindow window, @Mandatory String title, @Mandatory Runnable runnable) {
        ProgressDialog dialog = new ProgressDialog(window, title, runnable);
        dialog.start();
        dialog.setVisible(true);
        dialog.rethrow();
    }
}
