package cz.mg.backup.gui.dialog;

import cz.mg.annotations.classes.Component;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.backup.event.UserActionListener;
import cz.mg.backup.event.UserKeyPressListener;
import cz.mg.backup.event.UserWindowClosingListener;
import cz.mg.backup.gui.MainWindow;
import cz.mg.panel.Panel;
import cz.mg.panel.settings.Alignment;
import cz.mg.panel.settings.Fill;

import javax.swing.*;
import java.awt.event.KeyEvent;

public @Component class TaskDialog extends JDialog {
    private static final int MARGIN = 8;
    private static final int PADDING = 8;

    private final @Mandatory Thread thread;

    public TaskDialog(@Mandatory MainWindow window, @Mandatory String title, @Mandatory Runnable task) {
        super(window, true);
        setTitle(title);
        Panel panel = new Panel(MARGIN, PADDING);
        panel.addVertical(new JLabel("Task processing in progress ..."));
        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(new UserActionListener(this::cancel));
        cancelButton.addKeyListener(new UserKeyPressListener(this::onKeyPressed));
        panel.addVertical(cancelButton, 0, 0, Alignment.MIDDLE, Fill.NONE);
        getContentPane().add(panel);
        pack();
        setLocationRelativeTo(null);
        addWindowListener(new UserWindowClosingListener(this::cancel));
        addKeyListener(new UserKeyPressListener(this::onKeyPressed));
        thread = new Thread(task);
        thread.start();
    }

    private void onKeyPressed(int key) {
        if (key == KeyEvent.VK_ESCAPE) {
            cancel();
        }
    }

    private void cancel() {
        try {
            thread.interrupt();
            thread.join();
            SwingUtilities.invokeLater(this::dispose);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
