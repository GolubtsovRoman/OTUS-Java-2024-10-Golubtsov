package ru.otus.java.dev.pro.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class DonationException extends RuntimeException {

    public DonationException(String message) {
        super(message);
    }

}
