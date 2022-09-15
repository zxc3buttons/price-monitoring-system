package ru.tokarev.exception.userexception;

public class UserBadRequestException extends RuntimeException {

    private final String message;

    public UserBadRequestException(String message) {
        this.message = message;
    }

    public String getMessage() {
        return this.message;
    }
}
