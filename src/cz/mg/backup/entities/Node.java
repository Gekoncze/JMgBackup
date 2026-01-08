package cz.mg.backup.entities;

import cz.mg.annotations.classes.Entity;
import cz.mg.annotations.requirement.Required;
import cz.mg.annotations.storage.Part;
import cz.mg.annotations.storage.Value;
import cz.mg.collections.list.List;

import java.nio.file.Path;

public @Entity class Node {
    private Path path;
    private Path relativePath;
    private List<Exception> errors = new List<>();

    public Node() {
    }

    @Required @Value
    public Path getPath() {
        return path;
    }

    public void setPath(Path path) {
        this.path = path;
    }

    @Required @Value
    public Path getRelativePath() {
        return relativePath;
    }

    public void setRelativePath(Path relativePath) {
        this.relativePath = relativePath;
    }

    @Required @Part
    public List<Exception> getErrors() {
        return errors;
    }

    public void setErrors(List<Exception> errors) {
        this.errors = errors;
    }
}
