package cz.mg.backup.services;

import cz.mg.annotations.classes.Service;
import cz.mg.backup.components.Task;
import cz.mg.backup.exceptions.CancelException;

public @Service class CancelService {
    private static volatile @Service CancelService instance;

    public static @Service CancelService getInstance() {
        if (instance == null) {
            synchronized (Service.class) {
                if (instance == null) {
                    instance = new CancelService();
                }
            }
        }
        return instance;
    }

    private CancelService() {
    }

    public void check() {
        Thread thread = Thread.currentThread();
        if (thread instanceof Task) {
            Task task = (Task) thread;
            if (task.isCanceled()) {
                throw new CancelException();
            }
        }
    }
}
