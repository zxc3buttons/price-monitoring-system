package ru.tokarev.exception.marketplaceexception;

import javax.persistence.EntityExistsException;

public class MarketPlaceExistsException extends EntityExistsException {
    private final String message;

    public MarketPlaceExistsException(String message) {
        this.message = message;
    }

    public String getMessage() {
        return this.message;
    }
}
