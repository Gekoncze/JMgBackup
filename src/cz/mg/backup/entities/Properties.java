package cz.mg.backup.entities;

import cz.mg.annotations.classes.Entity;
import cz.mg.annotations.requirement.Optional;
import cz.mg.annotations.storage.Value;

import java.util.Date;

public @Entity class Properties {
    private Long size;
    private Date created;
    private Date modified;

    public Properties() {
    }

    @Optional @Value
    public Long getSize() {
        return size;
    }

    public void setSize(Long size) {
        this.size = size;
    }

    @Optional @Value
    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    @Optional @Value
    public Date getModified() {
        return modified;
    }

    public void setModified(Date modified) {
        this.modified = modified;
    }
}