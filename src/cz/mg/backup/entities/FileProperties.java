package cz.mg.backup.entities;

import cz.mg.annotations.classes.Entity;
import cz.mg.annotations.requirement.Required;
import cz.mg.annotations.storage.Value;

import java.util.Date;

public @Entity class FileProperties {
    private long size;
    private Date created;
    private Date modified;

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
    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    @Required @Value
    public Date getModified() {
        return modified;
    }

    public void setModified(Date modified) {
        this.modified = modified;
    }
}
