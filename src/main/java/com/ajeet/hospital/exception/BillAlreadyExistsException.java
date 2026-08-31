package com.ajeet.hospital.exception;

public class BillAlreadyExistsException extends RuntimeException {
    public BillAlreadyExistsException(String message) {
        super(message);
    }
}
