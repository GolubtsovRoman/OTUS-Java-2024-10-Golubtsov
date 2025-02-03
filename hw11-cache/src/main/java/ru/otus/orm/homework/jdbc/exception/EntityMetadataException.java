package ru.otus.orm.homework.jdbc.exception;

public class EntityMetadataException extends RuntimeException {

    public EntityMetadataException(Exception e) {
        super(e);
    }

    public EntityMetadataException(String message) {
        super(message);
    }

}
