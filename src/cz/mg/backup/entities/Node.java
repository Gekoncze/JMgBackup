package cz.mg.backup.entities;

import cz.mg.annotations.classes.Entity;
import cz.mg.annotations.requirement.Optional;
import cz.mg.annotations.requirement.Required;
import cz.mg.annotations.storage.Part;
import cz.mg.annotations.storage.Value;

import java.nio.file.Path;

public @Entity class Node {
    private Path path;
    private Path relativePath;
    private Exception error;

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

    @Optional @Part
    public Exception getError() {
        return error;
    }

    public void setError(Exception error) {
        this.error = error;
    }
}
