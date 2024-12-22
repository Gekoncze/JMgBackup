package cz.mg.backup.components;

import cz.mg.annotations.classes.Component;

import cz.mg.annotations.requirement.Mandatory;
import cz.mg.annotations.requirement.Optional;
import cz.mg.backup.exceptions.CancelException;

public @Component class Progress {
    private final @Mandatory String description;
    private volatile long value;
    private volatile long limit;
    private volatile @Optional Progress next;

    public Progress(@Mandatory String description) {
        this.description = description;
    }

    public @Mandatory String getDescription() {
        return description;
    }

    public long getValue() {
        return value;
    }

    public void setValue(long value) {
        this.value = value;
    }

    public long getLimit() {
        return limit;
    }

    public void setLimit(long limit) {
        this.limit = limit;
    }

    public @Optional Progress getNext() {
        return next;
    }

    public void setNext(@Optional Progress next) {
        this.next = next;
    }

    public @Optional Double percent() {
        return limit > 0 ? (((double)value / (double)limit) * 100) : null;
    }

    public @Mandatory Progress nest(@Mandatory String name) {
        Progress subProgress = new Progress(name);
        setNext(subProgress);
        return subProgress;
    }

    public void step() {
        value++;
        checkStatus();
    }

    public void step(long size) {
        value += size;
        checkStatus();
    }

    public void checkStatus() {
        Task<?> task = Task.getCurrentTask();
        if (task != null) {
            if (task.getStatus() == Status.CANCELLED) {
                throw new CancelException();
            }
        }
    }
}
