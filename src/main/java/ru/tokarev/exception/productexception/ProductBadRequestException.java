package ru.tokarev.exception.productexception;

public class ProductBadRequestException extends RuntimeException {

    private final String message;

    public ProductBadRequestException(String message) {
        this.message = message;
    }

    public String getMessage() {
        return this.message;
    }
}
