package ru.tokarev.exception.productexception;

import javax.persistence.EntityExistsException;

public class ProductExistsException extends EntityExistsException {

    private final String message;

    public ProductExistsException(String message) {
        this.message = message;
    }

    public String getMessage() {
        return this.message;
    }
}
