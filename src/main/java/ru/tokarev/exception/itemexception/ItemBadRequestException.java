package ru.tokarev.exception.itemexception;

public class ItemBadRequestException extends RuntimeException {

    private final String message;

    public ItemBadRequestException(String message) {
        this.message = message;
    }

    public String getMessage() {
        return this.message;
    }
}
