package cz.mg.backup.components;

import cz.mg.annotations.classes.Component;

import cz.mg.annotations.requirement.Mandatory;
import cz.mg.annotations.requirement.Optional;
import cz.mg.backup.exceptions.CancelException;

/**
 * Class to track progress of a task with a description.
 * Limit represents the expected number of steps.
 * Limit should be estimated where possible.
 * Limit should be 0 for indefinite tasks.
 * Value represents the number of completed steps.
 * Value should be incremented after each step is completed.
 * Progress may nest when there is a subtask. Don't forget to un-nest when it finishes.
 * After each step the current {@link Task} is retrieved to check if it is cancelled.
 * When task is cancelled, a {@link CancelException} is thrown.
 */
public @Component class Progress {
    private volatile @Mandatory String description = "Initialization...";
    private volatile long value;
    private volatile long limit;
    private volatile @Optional Progress next;

    public Progress() {
    }

    public @Mandatory String getDescription() {
        return description;
    }

    public void setDescription(@Mandatory String description) {
        this.description = description;
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

    public @Mandatory Progress nest() {
        Progress nestedProgress = new Progress();
        setNext(nestedProgress);
        return nestedProgress;
    }

    public @Optional Progress unnest() {
        Progress nestedProgress = getNext();
        setNext(null);
        return nestedProgress;
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
