package cz.mg.backup.utilities;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.annotations.requirement.Optional;
import cz.mg.backup.entities.Directory;
import cz.mg.backup.entities.File;
import cz.mg.backup.entities.Node;
import cz.mg.backup.exceptions.CategorizedException;
import cz.mg.backup.exceptions.Category;

public @Service class Categories {
    public static @Optional Category get(@Mandatory Node node) {
        Exception e = node.getException();
        if (e == null) {
            return null;
        } else if (e instanceof CategorizedException ce) {
            return ce.getCategory();
        } else {
            return Category.ERROR;
        }
    }

    public static @Optional Category getInner(@Mandatory Directory directory) {
        Category category = null;

        for (Directory child : directory.getDirectories()) {
            category = union(category, get(child));
        }

        for (File child : directory.getFiles()) {
            category = union(category, get(child));
        }

        return category;
    }

    private static @Optional Category union(@Optional Category a, @Optional Category b) {
        int ao = a != null ? a.ordinal() : Integer.MAX_VALUE;
        int bo = b != null ? b.ordinal() : Integer.MAX_VALUE;
        return ao < bo ? a : b;
    }
}
