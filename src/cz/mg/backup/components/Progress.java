package cz.mg.backup.components;

import cz.mg.annotations.classes.Component;

public @Component class Progress {
    private long value;
    private long limit;

    public Progress() {
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

    public void increment() {
        value++;
    }
}
