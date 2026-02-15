package cz.mg.backup.gui.entities;

import cz.mg.annotations.classes.Entity;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.annotations.requirement.Optional;
import cz.mg.annotations.storage.Common;
import cz.mg.backup.entities.Directory;
import cz.mg.backup.entities.Node;

public @Entity class State {
    private Directory left;
    private Directory right;
    private Node details;

    public State() {
    }

    public State(Directory left, Directory right, Node details) {
        this.left = left;
        this.right = right;
        this.details = details;
    }

    @Optional @Common
    public Directory getLeft() {
        return left;
    }

    public void setLeft(Directory left) {
        this.left = left;
    }

    @Optional @Common
    public Directory getRight() {
        return right;
    }

    public void setRight(Directory right) {
        this.right = right;
    }

    @Optional @Common
    public Node getDetails() {
        return details;
    }

    public void setDetails(Node details) {
        this.details = details;
    }

    public @Optional Directory getDirectory(@Mandatory Side side) {
        return switch (side) {
            case LEFT -> left;
            case RIGHT -> right;
        };
    }

    public void setDirectory(@Optional Directory directory, @Mandatory Side side) {
        switch (side) {
            case LEFT -> left = directory;
            case RIGHT -> right = directory;
        }
    }
}
