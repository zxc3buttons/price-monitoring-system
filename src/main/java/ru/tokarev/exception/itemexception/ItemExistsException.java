package ru.tokarev.exception.itemexception;

import javax.persistence.EntityExistsException;

public class ItemExistsException extends EntityExistsException {

    private final String message;

    public ItemExistsException(String message) {
        this.message = message;
    }

    public String getMessage() {
        return this.message;
    }
}
