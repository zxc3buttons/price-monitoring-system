package ru.tokarev.exception.itemexception;

import javax.persistence.EntityNotFoundException;

public class ItemNotFoundException extends EntityNotFoundException {

    private final String message;

    public ItemNotFoundException(String message) {
        this.message = message;
    }

    public String getMessage() {
        return this.message;
    }
}
