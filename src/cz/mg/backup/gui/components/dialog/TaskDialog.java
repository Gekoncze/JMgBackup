package cz.mg.backup.gui.components.dialog;

import cz.mg.annotations.classes.Component;
import cz.mg.annotations.classes.Service;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.backup.components.Task;
import cz.mg.backup.exceptions.CancelException;
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

public @Component class TaskDialog extends Dialog {
    private static final int MARGIN = 8;
    private static final int PADDING = 8;

    private final @Service ButtonFactory buttonFactory = ButtonFactory.getInstance();

    private final @Mandatory Task task;
    private final @Mandatory Timer timer;

    private TaskDialog(@Mandatory MainWindow window, @Mandatory String title, @Mandatory Runnable runnable) {
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

        task = Task.run(() -> run(runnable));
        timer = new Timer(100, this::updateProgress);
    }

    private void run(@Mandatory Runnable runnable) {
        try {
            runnable.run();
        } finally {
            SwingUtilities.invokeLater(this::dispose);
        }
    }

    private void cancel() {
        try {
            task.setCanceled(true);
            task.join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private void closed() {
        timer.stop();
        rethrow();
    }

    private void rethrow() {
        if (task.getException() != null && !(task.getException() instanceof CancelException)) {
            throw task.getException();
        }
    }

    private void updateProgress(@Mandatory ActionEvent event) {
        try {
            // TODO
        } catch (Exception e) {
            timer.stop();
        }
    }

    public static void show(@Mandatory MainWindow window, @Mandatory String title, @Mandatory Runnable runnable) {
        new TaskDialog(window, title, runnable).setVisible(true);
    }
}
