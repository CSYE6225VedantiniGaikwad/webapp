package com.neu.csye6225.demo.exceptions;

public class CannotAccessException extends RuntimeException{
    public CannotAccessException(String message) {
        super(message);
    }
}
