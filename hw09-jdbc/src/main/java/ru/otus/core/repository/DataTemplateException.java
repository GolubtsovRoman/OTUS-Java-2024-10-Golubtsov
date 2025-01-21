package ru.otus.core.repository;

public class DataTemplateException extends RuntimeException {

    public DataTemplateException(String message, Throwable cause) {
        super(message, cause);
    }

    public DataTemplateException(String message) {
        super(message);
    }
}
