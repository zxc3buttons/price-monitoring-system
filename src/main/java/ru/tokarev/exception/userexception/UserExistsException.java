package ru.tokarev.exception.userexception;

import javax.persistence.EntityExistsException;

public class UserExistsException extends EntityExistsException {

    private final String message;

    public UserExistsException(String message) {
        this.message = message;
    }

    public String getMessage() {
        return this.message;
    }
}
