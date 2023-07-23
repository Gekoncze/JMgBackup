package cz.mg.backup.components;

import cz.mg.annotations.classes.Component;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.annotations.requirement.Optional;

public @Component class Task extends Thread {
    private boolean canceled = false;
    private @Optional RuntimeException exception;

    public Task(@Mandatory Runnable runnable) {
        super(runnable);
    }

    public boolean isCanceled() {
        return canceled;
    }

    public void setCanceled(boolean canceled) {
        this.canceled = canceled;
    }

    public @Optional RuntimeException getException() {
        return exception;
    }

    public void setException(@Optional RuntimeException exception) {
        this.exception = exception;
    }
}
