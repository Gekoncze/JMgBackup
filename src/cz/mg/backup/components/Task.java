package cz.mg.backup.components;

import cz.mg.annotations.classes.Component;
import cz.mg.annotations.requirement.Mandatory;

public @Component class Task extends Thread {
    private boolean canceled = false;

    public Task(@Mandatory Runnable runnable) {
        super(runnable);
    }

    public boolean isCanceled() {
        return canceled;
    }

    public void setCanceled(boolean canceled) {
        this.canceled = canceled;
    }
}
