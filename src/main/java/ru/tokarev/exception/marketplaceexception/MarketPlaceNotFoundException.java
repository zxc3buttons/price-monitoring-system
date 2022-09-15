package ru.tokarev.exception.marketplaceexception;

import javax.persistence.EntityNotFoundException;

public class MarketPlaceNotFoundException extends EntityNotFoundException {

    private final String message;

    public MarketPlaceNotFoundException(String message) {
        this.message = message;
    }

    public String getMessage() {
        return this.message;
    }
}
