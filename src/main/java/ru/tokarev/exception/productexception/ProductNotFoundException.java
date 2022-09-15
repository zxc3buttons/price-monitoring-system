package ru.tokarev.exception.productexception;

import javax.persistence.EntityNotFoundException;

public class ProductNotFoundException extends EntityNotFoundException {

    private final String message;

    public ProductNotFoundException(String message) {
        this.message = message;
    }

    public String getMessage() {
        return this.message;
    }
}
