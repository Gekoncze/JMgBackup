package cz.mg.backup.components;

import cz.mg.annotations.classes.Component;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.annotations.requirement.Optional;

public @Component class Task extends Thread {
    private final @Mandatory UnsafeRunnable runnable;
    private @Mandatory Progress progress = new Progress();
    private boolean canceled = false;

    private Task(@Mandatory UnsafeRunnable runnable) {
        super(runnable);
        this.runnable = runnable;
    }

    public @Mandatory Progress getProgress() {
        return progress;
    }

    public void setProgress(@Mandatory Progress progress) {
        this.progress = progress;
    }

    public boolean isCanceled() {
        return canceled;
    }

    public void setCanceled(boolean canceled) {
        this.canceled = canceled;
    }

    public @Optional RuntimeException getException() {
        return runnable.getException();
    }

    public void setException(@Optional RuntimeException exception) {
        runnable.setException(exception);
    }

    public static @Mandatory Task run(@Mandatory Runnable runnable) {
        Task task = new Task(new UnsafeRunnable(runnable));
        task.start();
        return task;
    }

    private static class UnsafeRunnable implements Runnable {
        private final @Mandatory Runnable runnable;
        private @Optional RuntimeException exception;

        public UnsafeRunnable(@Mandatory Runnable runnable) {
            this.runnable = runnable;
        }

        public @Optional RuntimeException getException() {
            return exception;
        }

        public void setException(@Optional RuntimeException exception) {
            this.exception = exception;
        }

        @Override
        public void run() {
            try {
                runnable.run();
            } catch (RuntimeException e) {
                setException(e);
            }
        }
    }
}
