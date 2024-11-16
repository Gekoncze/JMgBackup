package cz.mg.backup.components;

import cz.mg.annotations.classes.Component;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.annotations.requirement.Optional;
import cz.mg.backup.exceptions.CancelException;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public @Component class Task<R> {
    private static final ThreadLocal<Task<?>> currentTask = new ThreadLocal<>();

    private final @Mandatory Function<Progress, R> runnable;
    private final @Mandatory Progress progress;
    private @Mandatory Status status;
    private @Optional R result;
    private @Optional RuntimeException exception;
    private @Optional Thread thread;

    public Task(@Mandatory String description, @Mandatory Runnable runnable) {
        this(description, progress -> {
            runnable.run();
            return null;
        });
    }

    public Task(@Mandatory String description, @Mandatory Consumer<Progress> runnable) {
        this(description, progress -> {
            runnable.accept(progress);
            return null;
        });
    }

    public Task(@Mandatory String description, @Mandatory Supplier<R> runnable) {
        this(description, progress -> {
            return runnable.get();
        });
    }

    public Task(@Mandatory String description, @Mandatory Function<Progress, R> runnable) {
        this.runnable = runnable;
        this.progress = new Progress(description);
        this.status = Status.PENDING;
    }

    public synchronized @Mandatory Progress getProgress() {
        return progress;
    }

    public synchronized @Mandatory Status getStatus() {
        return status;
    }

    public synchronized @Optional R getResult() {
        return result;
    }

    public synchronized @Optional RuntimeException getException() {
        return exception;
    }

    public synchronized void start() {
        if (status == Status.PENDING) {
            thread = new Thread(this::compute);
            status = Status.RUNNING;
            thread.start();
        }
    }

    private void compute() {
        try {
            currentTask.set(this);
            result = runnable.apply(progress);
            status = Status.COMPLETED;
        } catch (RuntimeException e) {
            if (!(e instanceof CancelException)) {
                exception = e;
                status = Status.FAILED;
            }
        }
    }

    public synchronized void cancel() {
        status = Status.CANCELLED;
    }

    public synchronized void join() {
        try {
            if (thread != null) {
                thread.join();
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public static @Optional Task<?> getCurrentTask() {
        return currentTask.get();
    }
}
