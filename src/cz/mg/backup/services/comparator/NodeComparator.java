package cz.mg.backup.services.comparator;

import cz.mg.annotations.classes.Base;
import cz.mg.annotations.classes.Service;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.annotations.requirement.Optional;
import cz.mg.backup.entities.Node;
import cz.mg.backup.exceptions.CompareException;

public @Base @Service class NodeComparator {
    /**
     * Sets given exception to given node in case it does not have any exception set yet.
     */
    protected void setCompareException(@Mandatory Node node, @Mandatory Exception exception) {
        if (node.getException() == null) {
            node.setException(exception);
        }
    }

    /**
     * Removes compare exception from given node in case it is compare exception.
     */
    protected void clearCompareException(@Optional Node node) {
        if (node != null && node.getException() != null && node.getException() instanceof CompareException) {
            node.setException(null);
        }
    }
}
