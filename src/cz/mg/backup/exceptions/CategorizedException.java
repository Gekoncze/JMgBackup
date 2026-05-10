package cz.mg.backup.exceptions;

import cz.mg.annotations.classes.Error;

/**
 * Exceptions implementing this interface can be categorized.
 * This specifies how given exception is presented to the user.
 */
public @Error interface CategorizedException {
    Category getCategory();
}
