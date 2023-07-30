package cz.mg.backup.components;

import cz.mg.annotations.classes.Component;
import cz.mg.annotations.requirement.Optional;

public @Component class Progress {
    private long value;
    private long limit;
    private @Optional Progress parent;

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

    public @Optional Progress getParent() {
        return parent;
    }

    public void setParent(@Optional Progress parent) {
        this.parent = parent;
    }
}
