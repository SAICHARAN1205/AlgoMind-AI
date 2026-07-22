package com.algomind.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class AlgorithmNotSupportedException extends RuntimeException {
    public AlgorithmNotSupportedException(String message) {
        super(message);
    }
}
