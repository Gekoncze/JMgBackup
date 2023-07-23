package cz.mg.backup.gui.dialog;

import cz.mg.annotations.classes.Component;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.backup.components.Task;
import cz.mg.backup.event.UserActionListener;
import cz.mg.backup.event.UserEscapeKeyPressListener;
import cz.mg.backup.event.UserWindowClosedListener;
import cz.mg.backup.event.UserWindowClosingListener;
import cz.mg.backup.exceptions.CancelException;
import cz.mg.backup.gui.MainWindow;
import cz.mg.panel.Panel;
import cz.mg.panel.settings.Alignment;
import cz.mg.panel.settings.Fill;

import javax.swing.*;

public @Component class TaskDialog extends Dialog {
    private static final int MARGIN = 8;
    private static final int PADDING = 8;

    private final @Mandatory Task task;

    private TaskDialog(@Mandatory MainWindow window, @Mandatory String title, @Mandatory Runnable runnable) {
        super(window);
        setTitle(title);
        Panel panel = new Panel(MARGIN, PADDING);
        panel.addVertical(new JLabel("Task processing in progress ..."));
        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(new UserActionListener(this::cancel));
        panel.addVertical(cancelButton, 0, 0, Alignment.MIDDLE, Fill.NONE);
        getContentPane().add(panel);
        pack();
        setLocationRelativeTo(null);
        addWindowListener(new UserWindowClosingListener(this::cancel));
        addWindowListener(new UserWindowClosedListener(this::rethrow));
        addKeyListenerRecursive(this, new UserEscapeKeyPressListener(this::cancel));
        task = new Task(() -> run(runnable));
        task.start();
    }

    private void run(@Mandatory Runnable runnable) {
        try {
            runnable.run();
        } catch (RuntimeException e) {
            if (!(e instanceof CancelException)) {
                task.setException(e);
            }
        }
        SwingUtilities.invokeLater(this::dispose);
    }

    private void cancel() {
        try {
            task.setCanceled(true);
            task.join();
            SwingUtilities.invokeLater(this::dispose);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private void rethrow() {
        if (task.getException() != null) {
            throw task.getException();
        }
    }

    public static void show(@Mandatory MainWindow window, @Mandatory String title, @Mandatory Runnable runnable) {
        new TaskDialog(window, title, runnable).setVisible(true);
    }
}
