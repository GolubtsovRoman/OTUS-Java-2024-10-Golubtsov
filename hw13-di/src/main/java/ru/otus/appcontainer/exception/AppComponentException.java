package ru.otus.appcontainer.exception;

public class AppComponentException extends RuntimeException {

    public AppComponentException(String message, Throwable cause) {
        super(message, cause);
    }

    public AppComponentException(String message) {
        super(message);
    }

}
