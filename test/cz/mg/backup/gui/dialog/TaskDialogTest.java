package cz.mg.backup.gui.dialog;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.classes.Test;
import cz.mg.backup.gui.MainWindow;
import cz.mg.backup.gui.components.dialog.TaskDialog;
import cz.mg.backup.services.TaskService;

import javax.swing.*;

public @Test class TaskDialogTest {
    private static final boolean ERROR = true;

    public static void main(String[] args) {
        TaskDialogTest test = new TaskDialogTest();
        test.test();
    }

    private final @Service TaskService taskService = TaskService.getInstance();

    private void test() {
        MainWindow window = new MainWindow();
        SwingUtilities.invokeLater(() -> TaskDialog.show(window, "Test Task", this::testTask));
        window.setVisible(true);
    }

    private void testTask() {
        for (int i = 0; i < 5; i++) {
            taskService.update();
            System.out.println(System.currentTimeMillis());
            sleep();
        }

        if (ERROR) {
            throw new RuntimeException("ERROR!");
        } else {
            System.out.println("DONE!");
        }
    }

    private void sleep() {
        try {
            Thread.sleep(1000);
        } catch (Exception e) {
            System.out.println(e.getClass().getSimpleName());
        }
    }
}
