package cz.mg.backup.entities;

import cz.mg.annotations.classes.Entity;
import cz.mg.annotations.requirement.Required;
import cz.mg.annotations.storage.Value;

import java.time.Instant;

public @Entity class FileProperties {
    private long size;
    private Instant created;
    private Instant modified;

    public FileProperties() {
    }

    @Required @Value
    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    @Required @Value
    public Instant getCreated() {
        return created;
    }

    public void setCreated(Instant created) {
        this.created = created;
    }

    @Required @Value
    public Instant getModified() {
        return modified;
    }

    public void setModified(Instant modified) {
        this.modified = modified;
    }
}
