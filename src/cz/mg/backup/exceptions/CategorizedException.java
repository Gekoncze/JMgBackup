package cz.mg.backup.exceptions;

import cz.mg.annotations.classes.Error;
import cz.mg.annotations.requirement.Mandatory;

/**
 * Exceptions implementing this interface can be categorized.
 * This specifies how given exception is presented to the user.
 */
public @Error interface CategorizedException {
    @Mandatory Category getCategory();
}
