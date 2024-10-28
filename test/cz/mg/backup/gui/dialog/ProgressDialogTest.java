package cz.mg.backup.gui.dialog;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.classes.Test;
import cz.mg.backup.gui.MainWindow;
import cz.mg.backup.gui.dialogs.ProgressDialog;
import cz.mg.backup.services.TaskService;

import javax.swing.*;

public @Test class ProgressDialogTest {
    private static final boolean SIMULATE_FAILED_TASK = false;

    public static void main(String[] args) {
        ProgressDialogTest test = new ProgressDialogTest();
        test.test();
    }

    private final @Service TaskService taskService = TaskService.getInstance();

    private void test() {
        MainWindow window = new MainWindow();
        SwingUtilities.invokeLater(() -> ProgressDialog.show(window, "Test Task", this::testTask));
        window.setVisible(true);
    }

    private void testTask() {
        for (int i = 0; i < 5; i++) {
            taskService.update();
            System.out.println(System.currentTimeMillis());
            sleep();
        }

        if (SIMULATE_FAILED_TASK) {
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
