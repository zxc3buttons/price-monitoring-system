package ru.tokarev.exception.categoryexception;

import javax.persistence.EntityExistsException;

public class CategoryExistsException extends EntityExistsException {

    private final String message;

    public CategoryExistsException(String message) {
        this.message = message;
    }

    public String getMessage() {
        return this.message;
    }
}
