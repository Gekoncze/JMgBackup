package cz.mg.backup.services;

import cz.mg.annotations.classes.Service;
import cz.mg.backup.components.Status;
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
        Task task = Task.getCurrentTask();
        if (task != null) {
            if (task.getStatus() == Status.CANCELLED) {
                throw new CancelException();
            }
        }
    }
}
