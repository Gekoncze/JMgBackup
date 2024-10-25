package cz.mg.backup.components;

import cz.mg.annotations.classes.Component;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.annotations.requirement.Optional;

public @Component class Task {
    private static final ThreadLocal<Task> currentTask = new ThreadLocal<>();

    private final @Mandatory Runnable runnable;
    private @Optional Thread thread;
    private volatile @Optional RuntimeException exception;
    private volatile @Mandatory Status status;

    public Task(@Mandatory Runnable runnable) {
        this.runnable = runnable;
        this.status = Status.PENDING;
    }

    public synchronized @Mandatory Status getStatus() {
        return status;
    }

    private synchronized void setStatus(@Mandatory Status status) {
        this.status = status;
    }

    public synchronized @Optional RuntimeException getException() {
        return exception;
    }

    public synchronized void setException(@Optional RuntimeException exception) {
        this.exception = exception;
    }

    public synchronized void start() {
        if (status == Status.PENDING) {
            thread = new Thread(this::compute);
            setStatus(Status.RUNNING);
            thread.start();
        }
    }

    private void compute() {
        try {
            currentTask.set(this);
            runnable.run();
            setStatus(Status.COMPLETED);
        } catch (RuntimeException e) {
            setException(e);
            setStatus(Status.FAILED);
        }
    }

    public synchronized void cancel() {
        setStatus(Status.CANCELLED);
    }

    public void join() {
        try {
            if (thread != null) {
                thread.join();
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public static @Optional Task getCurrentTask() {
        return currentTask.get();
    }
}
