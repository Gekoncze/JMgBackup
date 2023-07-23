package cz.mg.backup.gui.dialog;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.classes.Test;
import cz.mg.backup.exceptions.CancelException;
import cz.mg.backup.gui.MainWindow;
import cz.mg.backup.services.CancelService;

import javax.swing.*;

public @Test class TaskDialogTest {
    public static void main(String[] args) {
        TaskDialogTest test = new TaskDialogTest();
        test.test();
    }

    private final @Service CancelService cancelService = CancelService.getInstance();

    private void test() {
        MainWindow window = new MainWindow();
        TaskDialog dialog = new TaskDialog(window, "Test Task", this::testTask);
        SwingUtilities.invokeLater(() -> dialog.setVisible(true));
        window.setVisible(true);
    }

    @SuppressWarnings("InfiniteLoopStatement")
    private void testTask() {
        try {
            while (true) {
                cancelService.check();
                System.out.println(System.currentTimeMillis());
                sleep();
            }
        } catch (CancelException e) {
            System.out.println("CANCELED!");
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
