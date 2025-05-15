package ru.otus.java.dev.pro.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class ValidateException extends RuntimeException {

    public ValidateException(String param, Object value) {
        super("Parameter '%s' isn't valid. Value=%s".formatted(param, value));
    }

    public ValidateException(String param, Object value, String reason) {
        super("Parameter '%s' isn't valid. Value=%s. Reason: %s".formatted(param, value, reason));
    }

}
