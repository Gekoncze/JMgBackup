package cz.mg.backup.entities;

import cz.mg.annotations.classes.Entity;
import cz.mg.annotations.requirement.Optional;
import cz.mg.annotations.requirement.Required;
import cz.mg.annotations.storage.Part;

public @Entity class File extends Node {
    private FileProperties properties;
    private Checksum checksum;

    public File() {
    }

    @Required @Part
    public FileProperties getProperties() {
        return properties;
    }

    public void setProperties(FileProperties properties) {
        this.properties = properties;
    }

    @Optional @Part
    public Checksum getChecksum() {
        return checksum;
    }

    public void setChecksum(Checksum checksum) {
        this.checksum = checksum;
    }
}
