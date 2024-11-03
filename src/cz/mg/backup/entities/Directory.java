package cz.mg.backup.entities;

import cz.mg.annotations.classes.Entity;
import cz.mg.annotations.requirement.Required;
import cz.mg.annotations.storage.Part;
import cz.mg.collections.list.List;


public @Entity class Directory extends Node {
    private DirectoryProperties properties;
    private List<Directory> directories = new List<>();
    private List<File> files = new List<>();

    public Directory() {
    }

    @Required @Part
    public DirectoryProperties getProperties() {
        return properties;
    }

    public void setProperties(DirectoryProperties properties) {
        this.properties = properties;
    }

    @Required @Part
    public List<Directory> getDirectories() {
        return directories;
    }

    public void setDirectories(List<Directory> directories) {
        this.directories = directories;
    }

    @Required @Part
    public List<File> getFiles() {
        return files;
    }

    public void setFiles(List<File> files) {
        this.files = files;
    }
}
