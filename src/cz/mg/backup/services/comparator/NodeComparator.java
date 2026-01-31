package cz.mg.backup.services.comparator;

import cz.mg.annotations.classes.Base;
import cz.mg.annotations.classes.Service;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.annotations.requirement.Optional;
import cz.mg.backup.entities.Node;
import cz.mg.backup.exceptions.CompareException;

public @Base @Service class NodeComparator {
    /**
     * Sets given error to given node in case it does not have any error set yet.
     */
    protected void setCompareError(@Mandatory Node node, @Mandatory Exception error) {
        if (node.getError() == null) {
            node.setError(error);
        }
    }

    /**
     * Removes compare error from given node in case it is compare exception.
     */
    protected void clearCompareError(@Optional Node node) {
        if (node != null && node.getError() != null && node.getError() instanceof CompareException) {
            node.setError(null);
        }
    }
}
