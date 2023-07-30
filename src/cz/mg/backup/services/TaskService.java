package cz.mg.backup.services;

import cz.mg.annotations.classes.Service;
import cz.mg.backup.components.Progress;
import cz.mg.backup.components.Task;
import cz.mg.backup.exceptions.CancelException;

public @Service class TaskService {
    private static volatile @Service TaskService instance;

    public static @Service TaskService getInstance() {
        if (instance == null) {
            synchronized (Service.class) {
                if (instance == null) {
                    instance = new TaskService();
                }
            }
        }
        return instance;
    }

    private TaskService() {
    }

    public void update() {
        Thread thread = Thread.currentThread();
        if (thread instanceof Task) {
            Task task = (Task) thread;

            Progress progress = task.getProgress();
            progress.setValue(progress.getValue() + 1);

            if (task.isCanceled()) {
                throw new CancelException();
            }
        }
    }
}
